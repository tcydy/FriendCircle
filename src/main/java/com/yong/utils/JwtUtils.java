package com.yong.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {
    private static final String SECRET_KEY = "c2hhLW5uLWlkaWVnZS1kaWEtb2RlZGlhLW5xdWFuLXNp";// 秘钥
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000;// 12小时

    //获取安全的签名密钥
    private static SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //生成Jwt令牌
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                // 推荐使用新的安全 Key 方式
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token 传入的 token
     * @return 解析出的 Claims 负载信息
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                // 传入签名密钥进行校验
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
