import datetime
from decimal import Decimal
from flask import request
from flask_jwt_extended import get_jwt_identity, jwt_required
from flask_restful import Resource

from mysql_connection import get_connection
from mysql.connector import Error
            

class CartResource(Resource):

    # 선택옵션 장바구니에 저장하는 api (어떤 꽃 선택했는지까지 저장)
    @jwt_required()
    def post(self):

        data = request.get_json()
        userId = get_jwt_identity()

        if 'packageId' not in data or 'sizeId' not in data or 'flowerIdList' not in data or not isinstance(data['flowerIdList'], list):
            return {"result":"fail"}, 400
        
      
        try:
            connection = get_connection()

            # 첫 번째 쿼리문
            query1 = '''insert into cart
                        (userId, packageId, sizeId)
                        value
                        (%s, %s, %s);
                        '''

            record = (userId, data['packageId'], data['sizeId'])

            cursor = connection.cursor()
            cursor.execute(query1, record)

            # 새로 삽입된 주문의 orderId 가져오기
            cartId = cursor.lastrowid

            flowerIdList = data['flowerIdList']

            i = 0
            for flowerId in flowerIdList : 
                
                flowerId = flowerIdList[i]

                # 두 번째 쿼리문
                query2 = '''insert into addFlower
                            (flowerId, cartId)
                            value
                            (%s, %s);
                            '''

                record = (flowerId, cartId)

                cursor = connection.cursor()
                cursor.execute(query2, record)

                connection.commit()
                i = i+1

            cursor.close()
            connection.close()


        except Error as e:
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500

        
        return {'result': 'success'}, 200
    

    # 사용자별 장바구니 가져오는 api (꽃 정보까지)
    @jwt_required()
    def get(self):

        userId = get_jwt_identity()
        
        # 2. DB 로 부터 데이터 가져오기
        try : 
            connection = get_connection()
            
            # 문자열 더하라는 뜻 '''+data변수명+'''
            query = '''
                    select userId, c.cartId, p.packagingType, p.packagePrice, s.sizeType, s.sizePrice, 
                            group_concat(
                                            distinct f.flowerName 
                                            order by f.flowerName 
                                            asc separator ',') as orderFlower,
                            sum(f.flowerPrice) as orderFlowerPrice, 
                            case
                            when group_concat(
                                            distinct f.flowerName 
                                            order by f.flowerName 
                                            asc separator ',') = '근조화환' then p.secondPackagePhotoUrl
                            else p.firstPackagePhotoUrl
                            end as packageUrl, c.quantity
                    from cart c
                    join package p
                        on c.packageId = p.packageId
                    join size s
                        on c.sizeId = s.sizeId
                    join addFlower af
                        on c.cartId = af.cartId
                    join flower f
                        on af.flowerId =  f.id
                    where userId = %s and c.status = 0
                    group by c.cartId;
                    '''
            
            # 튜플은 콤마가 있어야한다
            recode = (userId, )

            cursor = connection.cursor(dictionary=True)

            cursor.execute( query , recode )

            # 데이터를 가져와야한다.
            result_list = cursor.fetchall()

            for result in result_list:
                if 'orderFlowerPrice' in result and isinstance(result['orderFlowerPrice'], Decimal):
                    result['orderFlowerPrice'] = int(result['orderFlowerPrice'])

            # 튜플로는 보낼수 없다 그래서 커서에 dictionary=True 추가
            print(result_list)

            cursor.close()
            connection.close()

        except Error as e:
            if cursor is not None :
                cursor.close()
            if connection is not None :
                connection.close()
            return {'result':'fail', 'error':str(e)}, 500

        # 3. 응답 할 데이터를 JSON으로 만든다. 

        return {'cartList' : result_list,
                'count' : len(result_list),
                'result' : 'success'}


class CartStatusResource(Resource):
    
    # 장바구니의 상품이 존재하는지 확인하는 api
    @jwt_required()
    def get(self):

        userId = get_jwt_identity()

        try:
            connection = get_connection()
            
            query = '''
                    select group_concat(
                                    distinct cartId
                                    order by cartId 
                                    asc separator ',') as cartId, count(status) count
                    from cart 
                    where userId = %s and status = 0
                    group by status and userId;'''
            recode = (userId, )

            cursor = connection.cursor(dictionary=True)

            cursor.execute( query , recode )

            # 데이터를 가져와야한다.
            result_list = cursor.fetchall()

            if len(result_list) > 0:
                result = result_list[0]
            elif len(result_list) == 0:
                return{'result':'success', 'count':0}, 200

            print(result_list[0])
            
            cartId = int(result['cartId'])
            count = int(result['count'])

            print(result)

            cursor.close()
            connection.close()

        except Error as e :
            if cursor is not None :
                cursor.close()
            if connection is not None :
                connection.close()
            return {'result':'fail', 'error':str(e)}, 500
        
        return {'result':'success', 'cartId':cartId, 'count':count}



    
class CartInfoResource(Resource):

    # 장바구니 결제 완료시 변경    
    @jwt_required()
    def put(self, cartId):
        try:
            user_id = get_jwt_identity()

            connection = get_connection()
            cursor = connection.cursor()

            # quantity 조건을 포함한 UPDATE 쿼리
            update_query = '''
                UPDATE cart
                SET status = '1'
                WHERE userId = %s AND cartId = %s
            '''

            update_record = (user_id, cartId)  
            cursor.execute(update_query, update_record)
            connection.commit()

            cursor.close()
            connection.close()


        except Error as e:
            print(f"SQL 쿼리 실행 중 오류 발생: {e}")
            if cursor:
                cursor.close()
            if connection:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500
        
        return {'result': 'success'}, 200
    


    # 장바구니 삭제하는 api
    @jwt_required()
    def delete(self, cartId):

        userId = get_jwt_identity()

        print(userId)
        print(cartId)

        try : 
            connection = get_connection()
            cursor = connection.cursor()

            # 첫번째 쿼리 addFlower 삭제
            query1 = '''
                    delete from addFlower
                    where cartId = %s;
                    '''

            record1 = ( cartId, )

            cursor.execute(query1, record1)
            connection.commit()

            # 영향 받은 행 수 확인
            affected_rows1 = cursor.rowcount
            print(f"영향 받은 행 수: {affected_rows1}")

            # 두번째 쿼리 cart 삭제
            query2 = '''
                    delete from cart
                    where userId = %s and cartId = %s;
                    '''

            record2 = ( userId, cartId)
            cursor.execute(query2, record2)
            connection.commit()
            
            cursor.close()
            connection.close()

        except Error as e:
            print(f"SQL 쿼리 실행 중 오류 발생: {e}")
            if cursor:
                cursor.close()
            if connection:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500
        
        return {'result': 'success'}, 200
    

class CartAddResource(Resource):
    
    # 카트 주문 수량 1 추가하는 api
    @jwt_required()
    def put(self, cartId):

        userId = get_jwt_identity()

        try:
            connection = get_connection()
            cursor = connection.cursor()

            query = '''
                    update cart
                    set quantity = quantity + 1
                    where userId = %s and cartId = %s;
                    '''
            record = ( userId, cartId)

            cursor.execute(query, record)
            connection.commit()

            cursor.close()
            connection.close()

        except Error as e:
            print(f"SQL 쿼리 실행 중 오류 발생: {e}")
            if cursor:
                cursor.close()
            if connection:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500
        
        return {'result': 'success'}, 200



class CartSubResource(Resource):

    # 카트 주문 수량 1 빼는 api
    @jwt_required()
    def put(self, cartId):

        userId = get_jwt_identity()

        try:
            connection = get_connection()
            cursor = connection.cursor()

            query = '''
                    update cart
                    set quantity = quantity - 1
                    where userId = %s and cartId = %s;
                    '''
            record = ( userId, cartId)

            cursor.execute(query, record)
            connection.commit()

            cursor.close()
            connection.close()

        except Error as e:
            print(f"SQL 쿼리 실행 중 오류 발생: {e}")
            if cursor:
                cursor.close()
            if connection:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500
        
        return {'result': 'success'}, 200
            
            

            