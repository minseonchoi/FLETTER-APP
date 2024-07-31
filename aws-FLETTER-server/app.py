from flask import Flask
from flask_jwt_extended import JWTManager
from flask_restful import Api
import serverless_wsgi
from config import Config
from resources.cart import CartAddResource, CartInfoResource, CartResource, CartStatusResource, CartSubResource
from resources.chatGPT import TextMassageMaker
from resources.flowers import FlowerNameResource, FlowerResource, FlowersListResource
from resources.order import BasketFlowerOptionResource, BunchFlowerOptionResource, OneFlowerOptionResource, PackageListResource, WreathFlowerOptionResource
from resources.orderList import OrderDeleteResource, OrderInfoResource, OrderListResource, OrderResource
from resources.user import UserRegisterResource
from resources.user import ProfileResource
from resources.user import UserLoginResource, UserLogoutResource, UserRegisterResource
from resources.user import jwt_blacklist
from resources.wish import LikeResource, wishResource

app = Flask(__name__)

# 환경변수 셋팅
app.config.from_object(Config)

# JWT 매니저 초기화
jwt = JWTManager(app)

#로그아웃된 토큰으로 요청하는 경우, 처리하는 함수 작성
@jwt.token_in_blocklist_loader
def check_if_token_is_revoked(jwt_header,jwt_payload):
  jti=jwt_payload['jti']
  return jti in jwt_blacklist

api = Api(app)

# Entry Point
# 회원
api.add_resource(UserLoginResource,'/user/login')
api.add_resource(UserRegisterResource,'/user/register')
api.add_resource(UserLogoutResource,'/user/logout')
api.add_resource(ProfileResource,'/profile')
api.add_resource(LikeResource,'/flower/<int:flower_id>/like')
api.add_resource(wishResource,'/wish')

# 꽃 정보
api.add_resource(FlowersListResource , '/flowers')
api.add_resource(FlowerResource , '/flowers/<int:flowerId>')
api.add_resource(FlowerNameResource , '/flowers/name')
api.add_resource(TextMassageMaker , '/flowers/ai')

# 장바구니 api
api.add_resource(CartResource,'/cart')
api.add_resource(CartStatusResource,'/cartStatus')
api.add_resource(CartInfoResource,'/cart/<int:cartId>')
api.add_resource(OrderResource,'/orderList')
api.add_resource(CartAddResource,'/cartadd/<int:cartId>')
api.add_resource(CartSubResource,'/cartsub/<int:cartId>')

# 주문 옵션 가져오는 api
api.add_resource(PackageListResource , '/order/package')
api.add_resource(BunchFlowerOptionResource , '/order/bunchflower')
api.add_resource(BasketFlowerOptionResource , '/order/basketflower')
api.add_resource(OneFlowerOptionResource , '/order/oneflower')
api.add_resource(WreathFlowerOptionResource , '/order/wreathflower')

# 주문 api
api.add_resource(OrderInfoResource,'/order/<int:orderId>')
api.add_resource(OrderDeleteResource,'/order/<int:order_id>')
api.add_resource(OrderListResource,'/order/list')



def handler(event, context):
    return serverless_wsgi.handle_request(app, event, context)


if __name__ == '__main__':
    app.run()