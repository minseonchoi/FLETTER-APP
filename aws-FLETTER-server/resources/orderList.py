import datetime
from flask import request
from flask_jwt_extended import get_jwt_identity, jwt_required
from flask_restful import Resource

from mysql_connection import get_connection
from mysql.connector import Error


class OrderResource(Resource):

    # 주문내역 1개 저장하는 api
    @jwt_required()
    def post(self):
        try:
            data = request.get_json()
            user_id = get_jwt_identity()
            cart_id = data.get('cartId')
            total_price = data.get('totalPrice')
            payment_method = data.get('paymentMethod')
            comment = data.get('comment')
            status = data.get('status')
            address = data.get('address')
            receive = data.get('receive')
            reservation_date = data.get('reservationDate')

            if not (cart_id and total_price and payment_method and address and receive and reservation_date):
                return {'error': 'Required fields are missing'}, 400

            connection = get_connection()
            cursor = connection.cursor()

            insert_query = '''
                INSERT INTO orders (userId, cartId, totalPrice, paymentMethod, comment, status, address, receive, reservationDate)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            '''
            insert_record = (user_id, cart_id, total_price, payment_method, comment, status, address, receive, reservation_date)
            cursor.execute(insert_query, insert_record)
            connection.commit()

            # 새로 삽입된 주문의 orderId 가져오기
            order_id = cursor.lastrowid

            # 타임스탬프와 orderId를 결합하여 order_number 생성 "년도/월/일"
            timestamp = datetime.datetime.now().strftime('%Y%m%d')
            order_number = f"{timestamp}-{order_id}"

            # 두 번째 쿼리: 생성된 order_number로 주문 업데이트
            update_query = '''
                UPDATE orders
                SET orderNumber = %s
                WHERE orderId = %s
            '''
            update_record =(order_number,order_id)
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
        
        return {'result': 'success', 'orderId': order_id, 'orderNumber': order_number}, 200
    
class OrderListResource(Resource):

    # 주문 내역 가져오는 api
    @jwt_required()
    def get(self):

        userId = get_jwt_identity()

        # 2. DB 로 부터 데이터 가져오기
        try : 
            connection = get_connection()
            
            query = '''
                    select o.orderId, o.createdAt, o.status, p.packagingType, 
                            group_concat(
                                                        distinct f.flowerName 
                                                        order by o.orderId
                                                        asc separator ',') as orderFlower,
                            c.quantity, s.sizeType, o.totalPrice, o.receive,
                            case
                            when group_concat(
                                            distinct f.flowerName 
                                            order by f.flowerName 
                                            asc separator ',') = '근조화환' then p.secondPackagePhotoUrl
                            else p.firstPackagePhotoUrl
                            end as packageUrl
                    from orders o
                    join user u
                        on o.userId = u.id
                    join cart c
                        on o.cartId = c.cartId
                    join package p
                        on c.packageId = p.packageId
                    join size s
                        on c.sizeId = s.sizeId
                    join addFlower af
                        on o.cartId = af.cartId
                    join flower f
                        on af.flowerId =  f.id
                    where o.userId = %s
                    group by o.orderId
                    order by o.createdAt desc; 
                    '''

            recode = (userId, )

            cursor = connection.cursor(dictionary=True)

            cursor.execute( query , recode )

            # 데이터를 가져와야한다.
            result_list = cursor.fetchall()

            i = 0
            for row in result_list:
                result_list[i]['createdAt'] = row['createdAt'].isoformat()
                i = i + 1

            for item in result_list:
                item['createdAt'] = item['createdAt'].split('T')[0]

            cursor.close()
            connection.close()

        except Error as e:
            if cursor is not None :
                cursor.close()
            if connection is not None :
                connection.close()
            return {'result':'fail', 'error':str(e)}, 500

        # 3. 응답 할 데이터를 JSON으로 만든다. 

        return {'orderList' : result_list,
                'count' : len(result_list),
                'result' : 'success'}
    


class OrderInfoResource(Resource):

    # 주문 상세 내역 가져오는 api
    @jwt_required()
    def get(self, orderId):

        userId = get_jwt_identity()
        print(orderId)
        
        # 2. DB 로 부터 데이터 가져오기
        try : 
            connection = get_connection()
            
            query = '''
                    select o.status, o.orderNumber, o.createdAt, u.userName, u.phone, o.address, p.packagingType, 
                            group_concat(
                                                distinct f.flowerName 
                                                order by f.flowerName 
                                                asc separator ',') as orderFlower,
                            o.reservationDate, o.comment, o.paymentMethod, o.totalPrice, o.receive
                    from orders o
                    join user u
                        on o.userId = u.id
                    join cart c
                        on o.cartId = c.cartId
                    join package p
                        on c.packageId = p.packageId
                    join size s
                        on c.sizeId = s.sizeId
                    join addFlower af
                        on o.cartId = af.cartId
                    join flower f
                        on af.flowerId =  f.id
                    where o.orderId = %s and o.userId = %s;
                    '''

            recode = (orderId, userId)

            cursor = connection.cursor(dictionary=True)

            cursor.execute( query , recode )

            # 데이터를 가져와야한다.
            result_list = cursor.fetchall()

            i = 0
            for row in result_list:
                result_list[i]['createdAt'] = row['createdAt'].isoformat()
                result_list[i]['reservationDate'] = row['reservationDate'].isoformat()
                i = i + 1

            for item in result_list:
                item['createdAt'] = item['createdAt'].split('T')[0]

            for item in result_list:
                date = item['reservationDate'].split('T')[0]
                hour = item['reservationDate'].split('T')[1].split(':')[0]
                minute = item['reservationDate'].split('T')[1].split(':')[1]

                item['reservationDate'] = date + " " + hour + ":" + minute

            cursor.close()
            connection.close()

        except Error as e:
            if cursor is not None :
                cursor.close()
            if connection is not None :
                connection.close()
            return {'result':'fail', 'error':str(e)}, 500

        # 3. 응답 할 데이터를 JSON으로 만든다. 

        return {'orderInfo' : result_list,
                'result' : 'success'}
    

class OrderDeleteResource(Resource):

    # 주문 정보 삭제하는 api
    @jwt_required()
    def delete(self,order_id):
        user_id = get_jwt_identity()
        try:
            connection = get_connection()
            cursor = connection.cursor(dictionary=True)
            query='''delete
                    from orders
                    where orderId = %s and userId = %s; '''
            record = (order_id,user_id)
            cursor=connection.cursor()
            cursor.execute(query,record)
            connection.commit()
            cursor.close()
            connection.close()

        except Error as e :
                if cursor is not None:
                    cursor.close()
                if connection is not None:
                    connection.close()
                return{'result':'fail'},500
        return{'result':'success'}










