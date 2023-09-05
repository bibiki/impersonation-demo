What is this
============

Sometimes a user with some role wants to see the app as another user with possibly other role. For example a superuser wants to see the app as a simpler user. This is called user impersonation but sometimes people refer to it as sign-in-as. This repository contains code to implement user impersonation in a Spring Boot application using Spring Security and Keycloak.

The correct way to do it
========================

If you use Keycloak to authenticate your app's users and you have admin access to Keycloak, the correct way to impersonate them requires 0 lines of code. You simply login on Keycloak as admin, navigate to users, and select the user that you want to impersonate. In the upper left corner, there will be an Actions button. Select impersonate, and voila, you are impersonating. Now in that same browser instance, navigate to your app and you will have logged in as the impersonated user. If you are already logged in on your app, this may malfunction so it is best that you log out of your app before trying this.

But if you really really want to write code, then this repo shows how you can do that.

Keycloak
========

It is just an off-the-shelf piece of software that allows you to have a central managment for your users. It then can authenticate users for your app using various schemes.

To configure Keycloak for this sample app, I used this link https://developers.redhat.com/articles/2023/07/24/how-integrate-spring-boot-3-spring-security-and-keycloak

As a result, this code uses what is refered to as OIDC authentication.

If you want to try it
=====================

Configure Keycloak as described in the link above. My configuration uses basic-realm for realm name and basic-client for client name. After that, add a user with the email of keycloak@keycloak.com to yourkeycloak server. Once you have created your client you will be provided with a client secret by your keycloak server. You will need that to put it in application.properties file in this code base. If you have chosen different names for realm and client then you will have to use those instead of what I used in the application.properties file.

On the other side, the app repository assumes that you have a copy of some info of your users in the backend of the application also. In particular, this code has a MyUserDetailsService.java file that stands for a database of users. There are four users: auser, buser, superuser, and keycloak@keycloak.com user.

auser has role admin but no role user, and buser has role user but no role admin. There are /admin and /user endpoints. /admin is accessible only for users with role admin and /user is accessible only for users with role user.

So, login using keycloak@keycloak.com user. If you navigate to localhost:8081/ you will be greeted with a message saying "hello keycloak@keycloak.com".

Navigate to /switching and specify the user you want to impersonate. Say for example auser. This will take you to localhost:8081/ and you will be greeted with a message saying "hello auser".

Now, you may navigate to /admin and you will be shown a message saying "admin". If you, however, navigate to /user you will get a 403 http status indicating that you have no permission to view that endpoint.

You may logout now and log back in. This time, you may chose to impersonate buser. The interesting part is that buser should get a 403 http status if buser tries to access /admin, and buser will see a message saying "user" if buser hits /user endpoint.

This is to show that user roles and permissions function well with this implementation of impersonation.

NOTE
====

This code does not include any protection of the user impersonation functionality. Any user can impersonate any other user with this configuration of Spring Security. If you base your solution in this code, you will need to make sure that you hide SwitchUserFilter endpoints behind proper roles.

The relevant confguration for impersonation is in ConfigureKeycloakSpringSecurity.java.

SwitchUserFilter is provided with a userDetailsService to be able to load the user being impersonated. This userDetailsService retrieves users from a database that is local to the application. Some other UserDetailsService could retrieve them from Keycloak, if Keycloak exposes an endpoint for that.
