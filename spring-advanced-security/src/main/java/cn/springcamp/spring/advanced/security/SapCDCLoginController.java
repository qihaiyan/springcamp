package cn.springcamp.spring.advanced.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Controller
public class SapCDCLoginController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/login/sap/cdc")
    public String loginWithSAP(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) throws JsonProcessingException {
        String idToken = request.getParameter("id_token");
        String jpk = restTemplate.getForObject(UriComponentsBuilder
                        .fromUriString("https://accounts.eu1.gigya.com/accounts.getJWTPublicKey")
                        .queryParam("ApiKey", "sap-api-key")
                        .queryParam("userKey", "sap-user-key")
                        .queryParam("secret", "{secret}")
                        .build(Map.of("secret", "sap-secret"))
                , String.class);

        Base64URL[] jwtPart;
        try {
            jwtPart = JWTParser.parse(idToken).getParsedParts();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid Signature.");
        }
        String kss = jwtPart[2].toString();
        String tokenData = jwtPart[0].toString() + "." + jwtPart[1].toString();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jpkMap = objectMapper.readValue(jpk, objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, String.class));
        String nString = jpkMap.get("n");
        final String expString = jpkMap.get("e");
        kss = kss.replace('-', '+'); // 62nd char of encoding
        kss = kss.replace('_', '/'); // 63rd char of encoding
        byte[] keySignature = Base64.getDecoder().decode(kss.getBytes());

        nString = nString.replace('-', '+'); // 62nd char of encoding
        nString = nString.replace('_', '/'); // 63rd char of encoding
        byte[] n = Base64.getDecoder().decode(nString.getBytes());
        byte[] e = Base64.getDecoder().decode(expString.getBytes());

        try {
            BigInteger nBigInt = new BigInteger(1, n);
            BigInteger eBigInt = new BigInteger(1, e);
            RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(nBigInt, eBigInt);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey rsa = fact.generatePublic(rsaPubKey);
            Signature rsaSig = Signature.getInstance("SHA256withRSA");
            rsaSig.initVerify(rsa);
            rsaSig.update(tokenData.getBytes(StandardCharsets.UTF_8));
            boolean result = rsaSig.verify(keySignature);
            if (!result) {
                log.error("Invalid Signature.");
            }
        } catch (Exception exception) {
            log.error("Invalid Signature.", exception);
        }
        return "redirect:/";
    }
}
