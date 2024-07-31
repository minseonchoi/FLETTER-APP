from flask import request
from flask_restful import Resource
from flask_jwt_extended import get_jwt_identity, jwt_required

from mysql_connection import get_connection
from mysql.connector import Error

class FlowersListResource(Resource):
    
    # 오늘의 꽃 가져오는 API
    @jwt_required(optional=True)
    def get(self) :
        
        # 1. 클라이언트가 보낸 데이터가 있으면 받아준다.
        
        userId = get_jwt_identity()

        # 2. DB로 부터 데이터를 가져온다.
        try : 
            connection = get_connection()
            
            # 문자열 더하라는 뜻 '''+data변수명+'''
            query = '''
                    select f.id as flowerId, flowerName, flowerPrice, flowerPhotoUrl, createdAt, updatedAt, status, origin, max(if( w.userid = %s , 1, 0)) as isWish
                    from flower f
                    left join wish w
                        on f.id = w.flowerId
                    group by f.id, f.flowerName, f.flowerPrice, f.flowerPhotoUrl, f.createdAt, f.updatedAt, f.status, f.origin;
                    '''
            
            # 사용자 ID가 None인 경우를 처리
            if userId is None:
                record = (0,)  # 일반 사용자를 위한 기본값
            else:
                record = (userId,)

            cursor = connection.cursor(dictionary=True)

            cursor.execute( query, record )

            # 데이터를 가져와야한다.
            result_list = cursor.fetchall()

            cursor.close()
            connection.close()

        except Error as e:
            # try에서 에러가 발생하면 클라이언트한테 리턴해준다.
            # json 은 숫자 아니면 문자열만 있다!!!
            if cursor is not None :
                cursor.close()
            if connection is not None :
                connection.close()
            return {'result':'fail', 'error':str(e)}, 500

        # 3. 클라이언트에게 json만들어서 응답한다.
        i = 0
        for row in result_list :
            result_list[i]['createdAt']= row['createdAt'].isoformat()
            result_list[i]['updatedAt']= row['updatedAt'].isoformat()
            i = i + 1

        return {'items' : result_list,
                'count' : len(result_list),
                'result' : 'success'}
    
    

class FlowerResource(Resource) :

    # 선택한 꽃 1개 가져오는 API
    def get(self, flowerId) :
        
        # 1. 클라이언트로부터 데이터를 받는다.
        print(flowerId)

        # 2. DB 로 부터 데이터 가져오기
        try : 
            connection = get_connection()

            print('커넥션 실행')
            
            # 문자열 더하라는 뜻 '''+data변수명+'''
            query = '''
                    select id, flowerName, flowerPhotoUrl, status, origin
                    from flower
                    where id = %s;
                    '''
            
            # 튜플은 콤마가 있어야한다
            recode = (flowerId, )
            
            print(recode)
            # 쿼리안에 변수를 직접 써서 record = () 는 없다

            cursor = connection.cursor(dictionary=True)

            cursor.execute( query , recode )

            # 데이터를 가져와야한다.
            result_list = cursor.fetchall()

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
        
        print()
        print(result_list)

        return {'items' : result_list,
                'result' : 'success'}
    

class FlowerNameResource(Resource):

    # 오늘의 꽃 이름만 가져오는 API
    def get(self) :

        # 1. 클라이언트로부터 데이터를 받는다.

        # 2. DB 로 부터 데이터 가져오기
        try : 
            connection = get_connection()

            print('커넥션 실행')
            
            # 문자열 더하라는 뜻 '''+data변수명+'''
            query = '''
                    select flowerName from flower;
                    '''
            
            # 튜플은 콤마가 있어야한다
            
            # 쿼리안에 변수를 직접 써서 record = () 는 없다

            cursor = connection.cursor(dictionary=True)

            cursor.execute( query )

            # 데이터를 가져와야한다.
            result_list = cursor.fetchall()
            
            flowerName_list = []
            i = 0
            for row in result_list:
                row = result_list[i].get('flowerName')
                flowerName_list.append(row)
                i = i + 1

            # 튜플로는 보낼수 없다 그래서 커서에 dictionary=True 추가

            cursor.close()
            connection.close()

        except Error as e:
            if cursor is not None :
                cursor.close()
            if connection is not None :
                connection.close()
            return {'result':'fail', 'error':str(e)}, 500
        
        flowerNameStr = str(flowerName_list).replace('[','').replace(']','')
        print(flowerNameStr)


        # 3. 응답 할 데이터를 JSON으로 만든다. 

        return {'flowerName' : flowerNameStr,
                'result' : 'success'}













