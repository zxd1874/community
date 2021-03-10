package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GiteeAccessTokenDTO;
import life.majiang.community.dto.GiteeUser;
import life.majiang.community.dto.GithubUser;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.provider.GiteeProvider;
import life.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private GiteeProvider giteeProvider;

    @Autowired
    private UserMapper userMapper;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.url}")
    private String redirectUrl;

    @Value("${gitee.client.id}")
    private String giteeClientId;

    @Value("${gitee.client.secret}")
    private String giteeClientSecret;

    @Value("${gitee.redirect.url}")
    private String giteeRedirectUrl;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletRequest request){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUrl);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(accessToken);

        if (user!=null){
            request.getSession().setAttribute("user",user);
            return "redirect:/";
        }else{
            return "redirect:/";
        }
    }

    @GetMapping("/GiteeCallback")
    public String giteeCallback(@RequestParam(name = "code")String code,
                                HttpServletRequest request){
        GiteeAccessTokenDTO giteeAccessTokenDTO = new GiteeAccessTokenDTO();
        giteeAccessTokenDTO.setClient_id(giteeClientId);
        giteeAccessTokenDTO.setClient_secret(giteeClientSecret);
        giteeAccessTokenDTO.setCode(code);
        giteeAccessTokenDTO.setRedirect_uri(giteeRedirectUrl);

        String accessToken = giteeProvider.getAccessToken(giteeAccessTokenDTO);
        GiteeUser giteeUser = giteeProvider.getUser(accessToken);

        if (giteeUser!=null){
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(giteeUser.getName());
            user.setAccount_id(String.valueOf(giteeUser.getId()));
            user.setGmt_create(System.currentTimeMillis());
            user.setGmt_modified(user.getGmt_create());
            userMapper.insert(user);

            request.getSession().setAttribute("giteeUser",giteeUser);
            return "redirect:/";
        }else{
            return "redirect:/";
        }
    }

}
