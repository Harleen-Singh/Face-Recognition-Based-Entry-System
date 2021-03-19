from rest_framework import serializers
from .models import *
from rest_framework.authentication import get_authorization_header


class AdminUserSerializer(serializers.ModelSerializer):
    class Meta:
        model=AdminUser
        fields='__all__'
        extra_kwargs={'password':{'write_only':True}}


    def create(self,**validated_data):
        admin=AdminUser.objects.create(**validated_data)
        print(validated_data['password'])
        admin.set_password(validated_data['password'])
        admin.save()
        return admin


class CustomUserSerializer(serializers.ModelSerializer):
    class Meta:
        model=CustomUser
        fields='__all__'
        extra_kwargs={'password':{'write_only':True}}

class UserImageSerializer(serializers.ModelSerializer):
    class Meta:
        model=UserImage
        fields='__all__'

class EntryRecordSerializer(serializers.ModelSerializer):
    class Meta:
        model=EntryRecord
        fields='__all__'

    
