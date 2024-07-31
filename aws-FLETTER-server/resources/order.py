from flask import request
from flask_jwt_extended import get_jwt_identity, jwt_required
from flask_restful import Resource

from mysql_connection import get_connection
from mysql.connector import Error


class PackageListResource(Resource):
    
    # 포장 데이터 가져오는 api
    def get(self):

        # 1. 클라이언트가 보낸 데이터가 있으면 받아준다.

        # 2. DB로 부터 데이터를 가져온다.
        try : 
            connection = get_connection()
            
            query = '''
                    select 
                    packageId,
                    packagingType, 
                    firstPackagePhotoUrl, 
                    secondPackagePhotoUrl,
                    thridPackagePhotoUrl, 
                    forthPackagePhotoUrl
                    from package;
                    '''

            cursor = connection.cursor(dictionary=True)

            cursor.execute( query )

            # 데이터를 가져와야한다.
            result_list = cursor.fetchall()

            # 튜플로는 보낼수 없다 그래서 커서에 dictionary=True 추가
            print(result_list)

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

        print()
        print(result_list)

        return {'packages' : result_list,
                'count' : len(result_list),
                'result' : 'success'}


class BunchFlowerOptionResource(Resource):

    # 꽃다발 옵션 데이터 가져오는 api
    @jwt_required()
    def get(self) :

        # 1. 클라이언트가 보낸 데이터가 있으면 받아준다.
        userId = get_jwt_identity();

        # 2. DB로 부터 데이터를 가져온다.
        try:
            connection = get_connection()

            cursor = connection.cursor(dictionary=True)

            # 첫 번째 쿼리 실행
            query1 = '''
                    select f.id as flowerId, f.flowerName, f.flowerPrice, max(if( w.userid = %s , 1, 0)) as isWish
                    from flower f
                    left join wish w
                        on f.id = w.flowerId
                    where flowerName not in ('근조화환','축하화환')
                    group by f.id, f.flowerName, f.flowerPrice, f.flowerPhotoUrl, f.createdAt, f.updatedAt, f.status, f.origin
                    order by isWish desc, f.id;
                    '''
            recode1 = (userId, )

            cursor.execute(query1, recode1)
            flower_list = cursor.fetchall()

            # 두 번째 쿼리 실행
            query2 = '''
                    select * from package
                    where packagingType in ('꽃다발');
                    '''
            cursor.execute(query2)
            package_list = cursor.fetchall()

            # 세 번째 쿼리 실행
            query3 = '''
                    select * from size
                    where sizeType not in ('1단', '2단', '3단');
                    '''
            cursor.execute(query3)
            size_list = cursor.fetchall()

            cursor.close()
            connection.close()

        except Error as e:
            # try에서 에러가 발생하면 클라이언트한테 리턴해준다.
            # json 은 숫자 아니면 문자열만 있다!!!
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500


        return {         
            'packageInfo': package_list,
            'size': size_list,   
            'flowers': flower_list,
            'size_count': len(size_list),
            'flower_count': len(flower_list),
            'result': 'success'
            }
    

class BasketFlowerOptionResource(Resource):

    # 꽃바구니 옵션 데이터 가져오는 api
    @jwt_required()
    def get(self) :

        # 1. 클라이언트가 보낸 데이터가 있으면 받아준다.
        userId = get_jwt_identity()

        # 2. DB로 부터 데이터를 가져온다.
        try:
            connection = get_connection()

            cursor = connection.cursor(dictionary=True)

            # 첫 번째 쿼리 실행
            query1 = '''
                    select f.id as flowerId, f.flowerName, f.flowerPrice, max(if( w.userid = %s , 1, 0)) as isWish
                    from flower f
                    left join wish w
                        on f.id = w.flowerId
                    where flowerName not in ('근조화환','축하화환')
                    group by f.id, f.flowerName, f.flowerPrice, f.flowerPhotoUrl, f.createdAt, f.updatedAt, f.status, f.origin
                    order by isWish desc, f.id;
                    '''
            recode1 = (userId, )

            cursor.execute(query1, recode1)
            flower_list = cursor.fetchall()

            # 두 번째 쿼리 실행
            query2 = '''
                    select * from package
                    where packagingType in ('꽃바구니');
                    '''
            cursor.execute(query2)
            package_list = cursor.fetchall()

            # 세 번째 쿼리 실행
            query3 = '''
                    select * from size
                    where sizeType not in ('1단', '2단', '3단');
                    '''
            cursor.execute(query3)
            size_list = cursor.fetchall()

            cursor.close()
            connection.close()

        except Error as e:
            # try에서 에러가 발생하면 클라이언트한테 리턴해준다.
            # json 은 숫자 아니면 문자열만 있다!!!
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500


        return {         
            'packageInfo': package_list,
            'size': size_list,   
            'flowers': flower_list,
            'size_count': len(size_list),
            'flower_count': len(flower_list),
            'result': 'success'
            }
    


class OneFlowerOptionResource(Resource):

    # 한송이 옵션 데이터 가져오는 api
    @jwt_required()
    def get(self) :

        # 1. 클라이언트가 보낸 데이터가 있으면 받아준다.
        userId = get_jwt_identity()

        # 2. DB로 부터 데이터를 가져온다.
        try:
            connection = get_connection()

            cursor = connection.cursor(dictionary=True)

            # 첫 번째 쿼리 실행
            query1 = '''
                    select f.id as flowerId, f.flowerName, f.flowerPrice, max(if( w.userid = %s , 1, 0)) as isWish
                    from flower f
                    left join wish w
                        on f.id = w.flowerId
                    where flowerName not in ('근조화환','축하화환')
                    group by f.id, f.flowerName, f.flowerPrice, f.flowerPhotoUrl, f.createdAt, f.updatedAt, f.status, f.origin
                    order by isWish desc, f.id;
                    '''
            recode1 = (userId, )

            cursor.execute(query1, recode1)
            flower_list = cursor.fetchall()

            # 두 번째 쿼리 실행
            query2 = '''
                    select * from package
                    where packagingType in ('꽃 한송이');
                    '''
            cursor.execute(query2)
            package_list = cursor.fetchall()


            cursor.close()
            connection.close()

        except Error as e:
            # try에서 에러가 발생하면 클라이언트한테 리턴해준다.
            # json 은 숫자 아니면 문자열만 있다!!!
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500


        return {        
            'packageInfo': package_list,
            'flowers': flower_list,
            'flower_count': len(flower_list),
            'result': 'success'
            }
    

class WreathFlowerOptionResource(Resource):

    # 화환 옵션 데이터 가져오는 api
    @jwt_required()
    def get(self) :

        # 1. 클라이언트가 보낸 데이터가 있으면 받아준다.
        userId = get_jwt_identity()

        # 2. DB로 부터 데이터를 가져온다.
        try:
            connection = get_connection()

            cursor = connection.cursor(dictionary=True)

            # 첫 번째 쿼리 실행
            query1 = '''
                    select f.id as flowerId, f.flowerName, f.flowerPrice, max(if( w.userid = %s , 1, 0)) as isWish
                    from flower f
                    left join wish w
                        on f.id = w.flowerId
                    where flowerName in ('근조화환','축하화환')
                    group by f.id, f.flowerName, f.flowerPrice, f.flowerPhotoUrl, f.createdAt, f.updatedAt, f.status, f.origin
                    order by isWish desc, f.id;
                    '''
            recode1 = (userId,)

            cursor.execute(query1, recode1)
            flower_list = cursor.fetchall()

            # 두 번째 쿼리 실행
            query2 = '''
                    select * from package
                    where packagingType in ('화환');
                    '''
            cursor.execute(query2)
            package_list = cursor.fetchall()

            # 세 번째 쿼리 실행
            query3 = '''
                    select * from size
                    where sizeType in ('1단', '2단', '3단');
                    '''
            cursor.execute(query3)
            size_list = cursor.fetchall()

            cursor.close()
            connection.close()

        except Error as e:
            # try에서 에러가 발생하면 클라이언트한테 리턴해준다.
            # json 은 숫자 아니면 문자열만 있다!!!
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return {'result': 'fail', 'error': str(e)}, 500


        return {         
            'packageInfo': package_list,
            'size': size_list,   
            'flowers': flower_list,
            'size_count': len(size_list),
            'flower_count': len(flower_list),
            'result': 'success'
            }
    

    

