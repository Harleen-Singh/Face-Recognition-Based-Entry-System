from django.urls import path
from .views import *

urlpatterns=[
    path('login/',Login.as_view(),name='login'),
    path('register/',Register.as_view(),name='register'),
    path('addImage/',AddImage.as_view(),name='addImage'),
    path('updateImage/',UpdateImage.as_view(),name='updateImage'),
    path('listUsers/',ListUsers.as_view(),name='listUsers'),
    path('recordEntry/',RecordEntry.as_view(),name='recordEntry'),
    path('viewEntries/',ViewEntries.as_view(),name='viewEntries')
]