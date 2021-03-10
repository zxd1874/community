package life.majiang.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.GiteeAccessTokenDTO;
import life.majiang.community.dto.GiteeUser;
import life.majiang.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GiteeProvider {

    @Value("${gitee.access.token}")
    private String accessToken;

    @Value("${gitee.com.user}")
    private String comUser;

    public String getAccessToken(GiteeAccessTokenDTO giteeAccessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(giteeAccessTokenDTO));
        Request request = new Request.Builder()
                .url(accessToken)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split(",")[0].split(":")[1].replace("\"","");
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GiteeUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(comUser)
                .header("Authorization","token "+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GiteeUser giteeUser = JSON.parseObject(string, GiteeUser.class);
            return giteeUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
