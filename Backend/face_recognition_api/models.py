from django.db import models

# Create your models here.

class AdminUser(models.Model):
    adminId=models.AutoField(primary_key=True)
    adminName=models.CharField(max_length=30, blank=False, null=False)
    adminEmail = models.EmailField(blank=False, null=False,unique=True)
    password = models.CharField(max_length=256, blank=False, null=False)

class CustomUser(models.Model):
    userId=models.AutoField(primary_key=True)
    userName=models.CharField(max_length=30, blank=False, null=False)
    userEmail = models.EmailField(blank=False, null=False,unique=True)
    password = models.CharField(max_length=256, blank=False, null=False)
    phoneNo = models.BigIntegerField(blank=True, null=True)

def user_directory_path(instance, filename):
    # file will be uploaded to MEDIA_ROOT/user_<id>/<filename>
    return '{0}_{1}/{2}'.format(instance.userId.userName,instance.userId.userId,filename)


class UserImage(models.Model):
    imageId=models.AutoField(primary_key=True)
    image=models.FileField(upload_to=user_directory_path)
    userId=models.ForeignKey(CustomUser,null=False,blank=False,on_delete=models.CASCADE)

class EntryRecord(models.Model):
    userId=models.ForeignKey(CustomUser,null=False,blank=False,on_delete=models.CASCADE)
    entryTime=models.DateTimeField()

    

    



