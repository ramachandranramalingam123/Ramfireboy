package com.beyontec.mdcp.authservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

//import com.beyontec.cra.service.impl.SpringSecurityUserDetailsImpl;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

   
	 @Value("${security.oauth2-server.user-id}")
	    private String clientId;

	    @Value("${security.oauth2-server.password}")
	    private String clientSecret;

	    @Value("${security.jwt.grant-type-password}")
	    private String grantType_Password;
	    
	    @Value("${security.jwt.grant-type-refresh_token}")
	    private String grantType_RefreshToken;
	    
	    @Value("${security.jwt.scope-read}")
	    private String scopeRead;

	    @Value("${security.jwt.scope-write}")
	    private String scopeWrite;

	    @Value("${security.jwt.resource-ids}")
	    private String resourceIds;

	    @Value("${security.jwt.access.token.validity}")
	    private int accessTokenValidity;

	    @Value("${security.jwt.refresh.token.validity}")
	    private int refreshTokenValidity;

	    @Autowired
	    private TokenStore tokenStore;

	    @Autowired
	    private JwtAccessTokenConverter accessTokenConverter;

	    @Autowired
	    private AuthenticationManager authenticationManager;
	    
	    /*  @Autowired
	    private TokenEnhancer customTokenEnhancer;
	    
	    
		@Autowired
	    private PasswordEncoder passwordEncoder;
	    
	    @Autowired
	    private SpringSecurityUserDetailsImpl userDetailsService;*/

	    @Override
	    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
	        configurer
	                .inMemory()
	                .withClient(clientId)
	                .secret(clientSecret)
	                .authorizedGrantTypes(grantType_Password,grantType_RefreshToken)
	                .scopes(scopeRead, scopeWrite)
	                .accessTokenValiditySeconds(accessTokenValidity)
	                .refreshTokenValiditySeconds(refreshTokenValidity)
	                .resourceIds(resourceIds);
	    }

   /* @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AccessTokenConverter accessTokenConverter;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("client")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                .scopes("read", "write")
                .autoApprove(true)
                .secret(passwordEncoder.encode("password"));
    }*/

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .accessTokenConverter(accessTokenConverter)
                .tokenStore(tokenStore);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("isAuthenticated()");
    }

    @Bean
    public TokenStore getTokenStore() {
        return new InMemoryTokenStore();
    }

}
