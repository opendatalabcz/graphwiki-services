package cz.gregetom.graphwiki.task.config

import cz.gregetom.graphwiki.commons.security.AuthenticationEntryPoint
import cz.gregetom.graphwiki.commons.security.JwtRequestFilter
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.web.CommonRequestMappingConstants
import cz.gregetom.graphwiki.task.web.RequestMappingConstants
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
                        CommonRequestMappingConstants.ENTRY_ACTIONS
                ).permitAll()
                .antMatchers(HttpMethod.POST,
                        RequestMappingConstants.TC_CREATE
                ).hasAnyAuthority(Roles.ROLE_TECHNICAL)
                .antMatchers(HttpMethod.GET,
                        RequestMappingConstants.TC_INBOX_PRIVATE,
                        RequestMappingConstants.TC_SELF
                ).hasAnyAuthority(Roles.ROLE_USER, Roles.ROLE_ADMIN)
                .antMatchers(HttpMethod.GET,
                        RequestMappingConstants.TC_INBOX_TEAM
                ).hasAnyAuthority(Roles.ROLE_ADMIN)
                .antMatchers(HttpMethod.PUT,
                        RequestMappingConstants.TC_ASSIGNMENT
                ).hasAnyAuthority(Roles.ROLE_ADMIN)
                .antMatchers(HttpMethod.DELETE,
                        RequestMappingConstants.TC_FINISHING
                ).hasAnyAuthority(Roles.ROLE_TECHNICAL)
                .antMatchers(HttpMethod.GET,
                        RequestMappingConstants.TC_OPEN
                ).hasAnyAuthority(Roles.ROLE_ADMIN)
                .anyRequest().denyAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
