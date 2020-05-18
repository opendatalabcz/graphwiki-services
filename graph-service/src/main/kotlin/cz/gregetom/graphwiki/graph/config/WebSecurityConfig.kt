package cz.gregetom.graphwiki.graph.config

import cz.gregetom.graphwiki.commons.security.AuthenticationEntryPoint
import cz.gregetom.graphwiki.commons.security.JwtRequestFilter
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.web.CommonRequestMappingConstants
import cz.gregetom.graphwiki.graph.web.RequestMappingConstants
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
                .antMatchers(HttpMethod.HEAD,
                        RequestMappingConstants.PERSON_FIND,
                        RequestMappingConstants.COMPANY_FIND,
                        RequestMappingConstants.RELATIONSHIP_FIND
                ).permitAll()
                .antMatchers(HttpMethod.GET,
                        CommonRequestMappingConstants.ENTRY_ACTIONS,
                        RequestMappingConstants.FULLTEXT_SEARCH,
                        RequestMappingConstants.GRAPH_VERTEX_FIND,
                        RequestMappingConstants.GRAPH,
                        RequestMappingConstants.RELATIONSHIP_FIND,
                        RequestMappingConstants.RELATIONSHIP_RELATED_FOR_VERTEX,
                        RequestMappingConstants.PERSON_FIND,
                        RequestMappingConstants.COMPANY_FIND,
                        RequestMappingConstants.COMPLAINT_FIND,
                        RequestMappingConstants.COMPLAINT_RELATED,
                        RequestMappingConstants.COMPLAINT_RELATED_FOR_GRAPH_ENTITY,
                        RequestMappingConstants.HISTORY,
                        RequestMappingConstants.ENTITY_REQUEST_FIND,
                        RequestMappingConstants.GRAPH_EXPORT_GRAPHML,
                        RequestMappingConstants.GRAPH_EXPORT_CLUEMAKER
                ).permitAll()
                .antMatchers(HttpMethod.GET,
                        RequestMappingConstants.COMPLAINT_SETUP
                ).hasAnyAuthority(Roles.ROLE_ADMIN, Roles.ROLE_USER)
                .antMatchers(HttpMethod.POST,
                        RequestMappingConstants.RELATIONSHIP_CREATE,
                        RequestMappingConstants.COMPLAINT_CREATE,
                        RequestMappingConstants.PERSON_CREATE,
                        RequestMappingConstants.COMPANY_CREATE
                ).hasAnyAuthority(Roles.ROLE_ADMIN, Roles.ROLE_USER)
                .antMatchers(HttpMethod.PUT,
                        RequestMappingConstants.PERSON_UPDATE,
                        RequestMappingConstants.COMPANY_UPDATE,
                        RequestMappingConstants.RELATIONSHIP_UPDATE,
                        RequestMappingConstants.ENTITY_REQUEST_STATE_TRANSITION,
                        RequestMappingConstants.COMPLAINT_STATE_TRANSITION,
                        RequestMappingConstants.PERSON_STATE_TRANSITION,
                        RequestMappingConstants.COMPANY_STATE_TRANSITION,
                        RequestMappingConstants.RELATIONSHIP_STATE_TRANSITION
                ).hasAnyAuthority(Roles.ROLE_ADMIN)
                .antMatchers(HttpMethod.DELETE,
                        RequestMappingConstants.PERSON_DELETE,
                        RequestMappingConstants.COMPANY_DELETE,
                        RequestMappingConstants.RELATIONSHIP_DELETE
                ).hasAnyAuthority(Roles.ROLE_ADMIN)
                .antMatchers(HttpMethod.PUT,
                        RequestMappingConstants.ENTITY_REQUEST_ASSIGNMENT,
                        RequestMappingConstants.COMPLAINT_ASSIGNMENT
                ).hasAnyAuthority(Roles.ROLE_TECHNICAL)
                .anyRequest().denyAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
