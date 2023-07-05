package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.parameter.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@ToString
public class OAuthAttributes {
    private final Map<String, Object> attributes;     // OAuth2 반환하는 유저 정보
    private final String nameAttributesKey;
    private final String name;
    private final String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributesKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of(String socialName, Map<String, Object> attributes) {
        if ("google".equals(socialName)) {
            return ofGoogle("sub", attributes);
        } else if ("naver".equals(socialName)) {
            return ofNaver("id", attributes);
        }

        return null;
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name(String.valueOf(response.get("name")))
                .email(String.valueOf(response.get("email")))
                .attributes(response)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    public Member toEntity(String provider) {
        return Member.builder()
                .memberName(name)
                .memberId(email)
                .memberDelete(false)
                .memberNickName(randomHangulName())
                .role(Role.USER)
                .memberProvider(provider)
                .build();
    }

    public String randomHangulName() {
        List<String> nick = Arrays.asList("기분나쁜", "기분좋은", "신바람나는", "상쾌한", "짜릿한", "그리운",
                "자유로운", "서운한", "울적한", "비참한", "위축되는", "긴장되는", "두려운", "당당한", "배부른", "수줍은", "창피한", "멋있는",
                "열받은", "심심한", "잘생긴", "이쁜", "시끄러운", "행복한", "희망찬", "우아한", "섬세한", "매혹적인", "화려한", "센스있는",
                "용감한", "밝은", "사랑스러운", "열정적인", "단순한", "섹시한", "매력적인", "귀여운", "도도한", "친절한", "민첩한", "신비로운",
                "말랑말랑한", "깔끔한", "청량한", "활기찬", "자상한", "포근한", "천진난만한", "늠름한", "정직한", "활발한", "성실한", "조용한",
                "유능한", "강인한", "신중한", "열심인", "명랑한", "재미있는", "낭만적인", "소박한", "근엄한", "용감한", "신사적인", "고상한",
                "고결한", "순수한", "예쁜", "멋진", "유쾌한", "카리스마있는", "긍정적인", "부드러운", "매너있는", "내추럴한", "지적인", "세련된",
                "극한의", "강렬한", "날카로운", "관능적인", "은은한", "빛나는");
        List<String> name = Arrays.asList("사자", "코끼리", "호랑이", "곰", "여우", "늑대", "너구리",
                "침팬치", "고릴라", "참새", "고슴도치", "강아지", "고양이", "거북이", "토끼", "앵무새", "하이에나", "돼지", "하마",
                "원숭이", "물소", "얼룩말", "치타", "악어", "기린", "수달", "염소", "다람쥐", "판다", "팽귄", "오리", "낙타",
                "뱀", "두꺼비", "원숭이", "바다사자", "알파카", "캥거루", "북극곰", "순록", "고슴도치", "개미핥기새", "코알라",
                "하이에나", "기린", "갈매기", "플라밍고", "카멜레온", "매", "캥거루", "말", "낙타", "햄스터", "토끼", "다람쥐",
                "원숭이", "판다", "물소", "비버", "오소리", "바다사자", "스컹크", "해달", "알파카", "침팬치", "팽귄", "코뿔소",
                "암모나이트", "스테고사우루스", "벨로포시스", "테리포르마", "디모르포돈", "드래곤", "유니콘", "사이보그", "로봇",
                "인공지능", "몬스터", "드래곤볼", "스폰지밥", "마리오", "소닉", "배트맨", "슈퍼맨", "아이언맨", "헐크", "스파이더맨");
        Collections.shuffle(nick);
        Collections.shuffle(name);
        return nick.get(0) + name.get(0);
    }

}
