from django.shortcuts import render
from .models import *
import jwt
from rest_framework.response import Response
from rest_framework.views import APIView
from django.contrib.auth.hashers import make_password, check_password
from rest_framework import status
from .serializers import *
from rest_framework.authentication import get_authorization_header
import os
# Create your views here.

class Login(APIView):
    def post(self,request):
        try:
            email=request.data.get('email')
            password=request.data.get('password')
            users=AdminUser.objects.filter(adminEmail=email)
            userType=''
            data={}
            if (len(users)!=0):
                user=users[0]
                print(user)
                userType='admin'
                UserSerializer=AdminUserSerializer(user)
                data=UserSerializer.data
            else:
                users=CustomUser.objects.filter(userEmail=email)
                if(len(users)!=0):
                    user=users[0]
                    userType='user'
                    UserSerializer=CustomUserSerializer(user)
                    data=UserSerializer.data
                else:
                    result = {
                    "Type": "Error",
                    "Message": "Email not registered",
                    "Data": None
                    }
                    return Response(data=result, status=status.HTTP_400_BAD_REQUEST)
            # print(data)
            if(check_password(password,user.password) or password==user.password):
                token=jwt.encode({'email':email},"secret", algorithm="HS256")
                print(data)
                data['token']=token
                data['userType']=userType
                result={
                    'Type':'Success',
                    'Message':'Login Successful',
                    'Data': data
                }
                return Response(data=result,status=status.HTTP_200_OK)
            else:
                result = {
                "Type": "Error",
                "Message": "Incorrect Password",
                "Data": None
                }
                return Response(data=result, status=status.HTTP_400_BAD_REQUEST)
        except:
            result = {
            "Type": "Error",
            "Message": "Email/Password not correct",
            "Data": None
            }
            return Response(data=result, status=status.HTTP_400_BAD_REQUEST)

class Register(APIView):
    def post(self,request):
        auth = get_authorization_header(request).split()
        print(auth)
        token=auth[1]
        print(token)
        obj = jwt.decode(token,"secret", algorithms=['HS256'])
        try:
            admin=AdminUser.objects.get(adminEmail=obj['email'])
            user={
                'userName':request.data.get('name'),
                'userEmail':request.data.get('email'),
                'password':make_password(request.data.get('password')),
                'phoneNo':request.data.get('phoneNo')
            }
            serializer=CustomUserSerializer(data=user)
            print(serializer.is_valid())
            if(serializer.is_valid()):
                serializer.save()
                response = {
                    "Type": "Success",
                    "Message": "User successfully registered",
                    "Data": serializer.data
                }
                return Response(response, status=status.HTTP_200_OK)
        except:
            response={
                "Type":"Error",
                "Message":"Incorrect Information",
                "Data":None
            }
            return Response(data=response, status=status.HTTP_400_BAD_REQUEST)


class AddImage(APIView):
    def post(self,request):
        auth = get_authorization_header(request).split()
        token=auth[1]
        obj = jwt.decode(token,"secret", algorithms=['HS256'])
        try:
            admin=AdminUser.objects.get(adminEmail=obj['email'])
            userId=request.data.get('userId')
            image=request.FILES['image']
            imgData={
                'image':image,
                'userId':userId
            }
            img=UserImageSerializer(data=imgData)
            print(img.is_valid())
            print(img.errors)
            if img.is_valid():
                img.save()
                response={
                        "Type": "Success",
                        "Message": "Image successfully uploaded",
                        "Data": img.data
                }
                return Response(response, status=status.HTTP_200_OK)
        except:
            response={
            "Type":"Error",
            "Message":"Incorrect Information",
            "Data":None
            }
            return Response(data=response, status=status.HTTP_400_BAD_REQUEST)

class UpdateImage(APIView):
    def post(self,request):
        auth = get_authorization_header(request).split()
        token=auth[1]
        obj = jwt.decode(token,"secret", algorithms=['HS256'])
        try:
            admin=AdminUser.objects.get(adminEmail=obj['email'])
            print('auth')
            userId=request.data.get('userId')
            imageId=request.data.get('imageId')
            image=request.FILES['image']
            imgs=UserImage.objects.filter(imageId=imageId)
            if (len(imgs)==0):
                response={
                    "Type":"Error",
                    "Message":"Incorrect Image Id",
                    "Data":None
                    }
                return Response(data=response, status=status.HTTP_400_BAD_REQUEST)
            else:
                os.remove(imgs[0].image.path)
                imgs[0].image=image
                imgs[0].save()
                response={
                "Type": "Success",
                "Message": "Image successfully changed",
                "Data": UserImageSerializer(imgs[0]).data
                }
                return Response(response, status=status.HTTP_200_OK)

        except:
            response={
            "Type":"Error",
            "Message":"Incorrect Information",
            "Data":None
            }
            return Response(data=response, status=status.HTTP_400_BAD_REQUEST)

    def delete(self,request):
        auth = get_authorization_header(request).split()
        token=auth[1]
        obj = jwt.decode(token,"secret", algorithms=['HS256'])
        try:
            admin=AdminUser.objects.get(adminEmail=obj['email'])
            userId=request.data.get('userId')
            imageId=request.data.get('imageId')
            imgs=UserImage.objects.filter(imageId=imageId)
            if (len(imgs)==0):
                response={
                    "Type":"Error",
                    "Message":"Incorrect Image Id",
                    "Data":None
                    }
                return Response(data=response, status=status.HTTP_400_BAD_REQUEST)
            else:
                os.remove(imgs[0].image.path)
                imgs[0].delete()
                response={
                "Type": "Success",
                "Message": "Image successfully deleted",
                "Data": None
                }
                return Response(response, status=status.HTTP_200_OK)

        except:
            response={
            "Type":"Error",
            "Message":"Incorrect Information",
            "Data":None
            }
            return Response(data=response, status=status.HTTP_400_BAD_REQUEST)

class ListUsers(APIView):
    def get(self,request):
        auth = get_authorization_header(request).split()
        token=auth[1]
        obj = jwt.decode(token,"secret", algorithms=['HS256'])
        try:
            admin=AdminUser.objects.get(adminEmail=obj['email'])
            users=CustomUser.objects.all()
            if (len(users)==0):
                response={
                    "Type":"Error",
                    "Message":"No user registered yet!",
                    "Data":None
                    }
                return Response(data=response, status=status.HTTP_200_OK)
            else:
                response={
                "Type": "Success",
                "Message": "List of Users",
                "Data": [CustomUserSerializer(user).data for user in users]
                }
                return Response(response, status=status.HTTP_200_OK)

        except:
            response={
            "Type":"Error",
            "Message":"Unauthorized user",
            "Data":None
            }
            return Response(data=response, status=status.HTTP_400_BAD_REQUEST)

class RecordEntry(APIView):
    def post(self,request):
        userId=request.data.get('userId')
        entryTime=request.data.get('entryTime')
        record={
            'userId':userId,
            'entryTime':entryTime
        }
  
        recordSerializer=EntryRecordSerializer(data=record)
        print(recordSerializer.is_valid())
        print(recordSerializer.errors)
        if(recordSerializer.is_valid()):
            recordSerializer.save()
            response={
                "Type": "Success",
                "Message": "Entry Recorded",
                "Data": recordSerializer.data
            }
            return Response(response, status=status.HTTP_200_OK)

        else:
            response={
            "Type":"Error",
            "Message":"Entry not recorded",
            "Data":None
            }
            return Response(data=response, status=status.HTTP_400_BAD_REQUEST)

class ViewEntries(APIView):
    def post(self,request):
        auth = get_authorization_header(request).split()
        token=auth[1]
        obj = jwt.decode(token,"secret", algorithms=['HS256'])
        try:
            admin=AdminUser.objects.get(adminEmail=obj['email'])
            userId=request.data.get('userId')
        except:
            user=CustomUser.objects.get(userEmail=obj['email'])
            userId=user.userId
        entries=EntryRecord.objects.filter(userId=userId)
        response={
        "Type": "Success",
        "Message": "List of entries",
        "Data": [EntryRecordSerializer(entry).data for entry in entries]
        }
        return Response(response, status=status.HTTP_200_OK)
            




















