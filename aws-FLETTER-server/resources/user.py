import datetime
from email_validator import EmailNotValidError
from flask_jwt_extended import create_access_token, get_jwt, get_jwt_identity, jwt_required
from flask_restful import Resource
from flask import request
from mysql.connector import Error
from email_validator import validate_email

from mysql_connection import get_connection
from utils import check_password, hash_password
 
class UserRegisterResource(Resource) :

    def post(self):
        # 1. 클라이언트가 보낸 데이터를 받아준다.
            data = request.get_json()
            print(data)
            # 2. 데이터가 모두 있는지 확인
            if data.get('email') is None or data.get('email').strip() == '' or \
                data.get('userName') is None or data.get('userName').strip() == '' or \
                data.get('password') is None or data.get('password').strip() == '' or \
                data.get('phone') is None or data.get('phone').strip() == '' :
                return {'result' : 'fail'},400

            # 3. 이메일주소 형식이 올바른지 확인한다.
            try :
                validate_email(data['email'])
            except EmailNotValidError as e :
                return {'result' : 'fail', 'error' : str(e)},400

            # 4. 비밀번호 길이가 유효한지 체크한다.
            #    예) 비번은 4자리 이상 12자리 이하!
            if len(data['password']) < 4 or len(data['password']) > 12:
                return {'result' : 'fail'}, 400

            # 5. 비밀번호를 암호화 한다. 
            password = hash_password( data['password'] )
            print(password)


            # 6. db에 저장한다.
            try:
                connection = get_connection()
                query = '''insert into user
                            (email, password, phone,userName)
                            values
                            (%s, %s, %s,%s);'''
                record = (data['email'], password, data['phone'],data['userName'])
                cursor = connection.cursor()
                cursor.execute(query, record)
                connection.commit()

                user_id=cursor.lastrowid

                cursor.close()
                connection.close()

            except Error as e :
                if cursor is not None:
                    cursor.close()
                if connection is not None:
                    connection.close()
                return {'result':'fail',
                        'error' : str(e)}, 500
            
        # 7. access token 을 만든다.
            access_token = create_access_token(user_id)

            return {'result' : 'success',
                'accessToken' : access_token}
    

class UserLoginResource(Resource) :    
    def post(self) :


        #1. 클라이언트에게 데이터를 받는다
        data=request.get_json()

        if 'email' not in data or 'password'not in data:
            return{'result':'fail'},400
        if data['email'].strip() == '' or data['password'].strip() == '' :
            return{'result':'fail'},400

        #2.db로부터 이메일에 해당하는 유저 정보를 가져온다.
        try:
            connection = get_connection()
            query='''select*
                    from user
                    where email = %s;
                    '''
            record = (data['email'],)
            cursor = connection.cursor(dictionary=True)
            cursor.execute(query,record)
            result_list = cursor.fetchall()
            print(result_list)

            cursor.close()
            connection.close()
        except Error as e :
            if cursor is not None:
                cursor.close()
            if connection is not None:
                connection.close()
            return{'result':'fail','error':str(e)},500


        #3.회원인지 확인한다.
        if result_list == [] :
            return{'result':'fail'},400

        #4.비밀번호를 체크한다.
        #유저가 입력한 비번 data['password']
        #db에 암호화된 비번
        #if result_list[0]['password']
        isCorrect = check_password(data['password'],result_list[0]['password'])
        if isCorrect == False :
            return{'return':'fail'},401


        #5.유저아이디를 가져온다.
        try:
            user_id = result_list[0]['id']
        except KeyError:
            print("Error: 'id' 키가 result_list[0]에 존재하지 않습니다.")
            user_id = None  # 혹은 적절한 기본값을 설정
        

        #6.jwt 토큰을 만든다.
        #유효기간 만료버전 :access_token = create_access_token(user_id,expires_delta=datetime.timedelta(minutes=3))
        access_token = create_access_token(user_id)

        #7.클라이언트에 응답한다.

        return {'return':'success' , 'access_token':access_token} 
    

# 로그아웃된 토큰을 저장할 set을 만든다.
jwt_blacklist = set()
class UserLogoutResource(Resource) :
    @jwt_required()
    def delete(self) :
        jti = get_jwt()['jti']
        jwt_blacklist.add(jti)
        return {'result':'success'}
    
class ProfileResource(Resource):

    # 유저 정보 가져오는 api
    @jwt_required()
    def get(self) :

        #1.클라이언트로부터 데이터 받는다
        user_id=get_jwt_identity()

        #2.db 쿼리한다.
        try :
            connection = get_connection()
            query = '''SELECT email, userName, phone, createdAt
                       FROM user
                       WHERE Id = %s;'''
            record = (user_id,)
            cursor = connection.cursor(dictionary=True)
            cursor.execute(query, record)
            result_list = cursor.fetchall()
        except Error as e:
            return {'result': 'fail', 'error': str(e)}, 500
        finally:
            if cursor:
                cursor.close()
            if connection:
                connection.close()

        # 3. 결과를 JSON으로 변환하기 전에 datetime 객체를 문자열로 변환
        for row in result_list:
            for key, value in row.items():
                if isinstance(value, datetime.date):
                    row[key] = value.isoformat()

        return {
            'user': result_list,
            'result': 'success'
        }

    