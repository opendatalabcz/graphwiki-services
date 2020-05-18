package cz.gregetom.graphwiki.comment.config

import cz.gregetom.graphwiki.comment.web.RequestMappingConstants
import cz.gregetom.graphwiki.commons.security.AuthenticationEntryPoint
import cz.gregetom.graphwiki.commons.security.JwtRequestFilter
import cz.gregetom.graphwiki.commons.security.enums.Roles
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class WebSecurityConfig(
        private val authenticationEntryPoint: AuthenticationEntryPoint,
        private val jwtRequestFilter: JwtRequestFilter
) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.GET,
                        RequestMappingConstants.CC_LIST
                ).permitAll()
                .antMatchers(HttpMethod.POST,
                        RequestMappingConstants.CC_CREATE,
                        RequestMappingConstants.CC_REPLY
                ).hasAnyAuthority(Roles.ROLE_USER, Roles.ROLE_ADMIN)
                .antMatchers(HttpMethod.PUT,
                        RequestMappingConstants.CC_UPDATE
                ).hasAnyAuthority(Roles.ROLE_USER, Roles.ROLE_ADMIN)
                .antMatchers(HttpMethod.DELETE,
                        RequestMappingConstants.CC_DELETE
                ).hasAnyAuthority(Roles.ROLE_USER, Roles.ROLE_ADMIN)
                .anyRequest().denyAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
