package posmy.argos.security;

import com.microsoft.azure.spring.autoconfigure.aad.AADAppRoleStatelessAuthenticationFilter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

import static org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toStaticResources;
import static org.springframework.security.config.http.SessionCreationPolicy.NEVER;

/**
 * @author Rashidi Zin
 */
@Configuration
@AllArgsConstructor
public class OAuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AADAppRoleStatelessAuthenticationFilter authenticationFilter;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring().requestMatchers(toStaticResources().atCommonLocations());
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(NEVER)
            .and()
                .authorizeRequests()
                    .requestMatchers(toAnyEndpoint()).permitAll()
                    .regexMatchers("/").permitAll()
                    .anyRequest().authenticated()
            .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
