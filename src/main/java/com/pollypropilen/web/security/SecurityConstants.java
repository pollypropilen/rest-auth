package com.pollypropilen.web.security;

public class SecurityConstants {
    public static final String SECRET = "233852831668995566935652187694852674928217721548765475962797885497025058535089157951249622569518379904007725587811878189365142180471775634358773106596044715437418136158370623696550325015833862565386906166928374157491403586067693851525209411428101765801845529581455721613009102496652375242659554762945826919393036966392381001745088441330969129966152971068103803154291245335864462829638969956837275889671827613628869997586580706563232378399878190880231547117777995685489807828959022873320728918222143984011113497475325816325520810115315934313924091336110972807341675540383009792424150414847052631638980286658268699";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 90_000_000; // ~ 24h
    public static final long FIRST_TOKEN_EXP_TIME = 600_000; // 10 min
    public static final String TOKEN_ID = "id";
    public static final String TOKEN_SUBJECT = "un";
    public static final String TOKEN_IP = "ui";
    public static final String TOKEN_ISSING = "uis";
    public static final String TOKEN_EXPIRE = "uex";
    public static final String TOKEN_AGENT = "ua";
}