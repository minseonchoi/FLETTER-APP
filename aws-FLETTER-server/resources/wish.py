import datetime
from flask import request
from flask_jwt_extended import get_jwt_identity, jwt_required
from flask_restful import Resource

from mysql_connection import get_connection
from mysql.connector import Error



''' 좋아요 추가 '''
class LikeResource(Resource):
    @jwt_required()
    def post(self,flower_id):
        user_id=get_jwt_identity()

        try:
            connection=get_connection()
            query='''insert into wish
                    (flowerId,userId)
                    values
                    (%s,%s);
                    '''
            record=(flower_id,user_id)
            cursor=connection.cursor()
            cursor.execute(query,record)
            connection.commit()
            cursor.close()
            connection.close()

        except Error as e:
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return {'result':'fail',
                    'error' : str(e)}, 500
        return {'result':'success'}
    
    '''좋아요 취소'''
    @jwt_required()
    def delete(self,flower_id):
        user_id=get_jwt_identity()

        try:
            connection=get_connection()
            query='''delete from wish
                    where flowerId=%s and userId = %s;
                    '''
            record=(flower_id,user_id)
            cursor=connection.cursor()
            cursor.execute(query,record)
            connection.commit()
            cursor.close()
            connection.close()

        except Error as e:
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return {'result':'fail',
                    'error' : str(e)}, 500
        return {'result':'success'}

class wishResource(Resource):
    @jwt_required()
    def get(self) :

        #1.클라이언트로부터 데이터 받는다
        user_id=get_jwt_identity()

        #2.db 쿼리한다.
        try :
            connection = get_connection()
            query = '''select f.id as flowerId, f.flowerName, f.flowerPhotoUrl, f.status, f.origin
                    from wish w
                    join flower f
                    on f.id = w.flowerId
                    where w.userId = %s;
                        '''
            record = (user_id,)
            cursor = connection.cursor(dictionary=True)
            cursor.execute(query,record)
            result_list = cursor.fetchall()
            cursor.close()
            connection.close()

        except Error as e :
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return{'result':'fail'},500
        
        #3.결과를 json으로 응답한다.
        for row in result_list:
            for key, value in row.items():
                if isinstance(value, datetime.date):
                    row[key] = value.isoformat()

        return { 'items' : result_list,
                'count' : len(result_list),
                  'result' : 'success'  }