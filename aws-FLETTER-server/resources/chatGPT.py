from aifc import Error
from flask import jsonify, request
from flask_restful import Resource
import openai
import os
from aifc import Error

from config import Config
from mysql_connection import get_connection



class TextMassageMaker(Resource):

    def post(self):	
        
        data = request.get_json()

        if 'reason' not in data or 'packageType' not in data or 'combination' not in data:
            return{'result':'fail', 'error':'Bad request'},400

        # 2. DB 로 부터 데이터 가져오기
        try : 
            connection = get_connection()

            print('커넥션 실행')
            
            # 문자열 더하라는 뜻 '''+data변수명+'''
            query = '''
                    select flowerName from flower;
                    '''

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

        reason = data['reason']
        packageItem = data['packageType']
        combination = data['combination']

        userPrompt = f"우리가 사용할 꽃은 ({flowerNameStr}) 이야. ({reason})을(를) 주제로 ({packageItem})를 ({combination})의 꽃을 조합해서 꽃말과 함께 추천해서 알려줘. 꼭! {combination}의 수를 꼭 지켜서 추천해줘야해. '한송이'는 꼭 한개의 꽃만 추천해줘. 꽃 조합은 1가지만 추천해줘야 하는데, 간단한 설명과 함께 부탁해"

        OPENAI_KEY = Config.GPT_AI_KEY

        try:
            openai.api_key = OPENAI_KEY

            response = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": "너는 꽃 조합을 추천해주는 플로리스트야."},
                    {"role": "user", "content": userPrompt}
                ],
                temperature=0.5,
                max_tokens=2048
            )
            print(response)
            responseData = response.choices[0].message['content']
            print("응답 메세지 : "+responseData)

            serverPrompt = "방금 추천한 꽃의 이름을 다른 말 없이 이름만 쉼표로 구분해서 알려줘. 절대 다른 말하지말고 꽃 이름만 말해."

            response2 = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": responseData},
                    {"role": "user", "content": serverPrompt}
                ],
                temperature=0.5,
                max_tokens=2048
            )
            print(response2)
            responseServer = response2.choices[0].message['content']
            print("응답 메세지 : "+responseServer)

        except openai.error.InvalidRequestError as e:
            return {'result': 'fail', 'InvalidRequestError': str(e)}, 500
        except openai.error.APIError as e:
            return {'result': 'fail', 'APIError': str(e)}, 500
        except openai.error.OpenAIError as e:
            return {'result': 'fail', 'OpenAIError': str(e)}, 500
        except Exception as e:
            return {'result': 'fail', 'error': str(e)}, 500
    

        return jsonify({"responseMessage": responseData, "responseServer":responseServer, "result": "success"})













