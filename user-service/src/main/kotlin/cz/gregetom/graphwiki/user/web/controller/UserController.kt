package cz.gregetom.graphwiki.user.web.controller

import cz.gregetom.graphwiki.api.user.api.UserApi
import cz.gregetom.graphwiki.api.user.model.CreateUserTO
import cz.gregetom.graphwiki.api.user.model.UserTO
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.user.service.UserService
import cz.gregetom.graphwiki.user.web.UserApiLinks
import cz.gregetom.graphwiki.user.web.mapper.UserMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class UserController(
        private val userMapper: UserMapper,
        private val userService: UserService
) : UserApi {

    @Transactional(readOnly = true)
    override fun findById(@Size(max = 50) @PathVariable id: String): ResponseEntity<UserTO> {
        return ResponseEntity.ok(userMapper.map(userService.findById(id)))
    }

    @Transactional
    override fun create(@Valid @RequestBody createUserTO: CreateUserTO): ResponseEntity<Unit> {
        val id = userService.register(createUserTO)
        return ResponseEntity
                .created(UserApiLinks.self(id).toUri())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .build()
    }

    @Transactional(readOnly = true)
    override fun loggedUser(): ResponseEntity<UserTO> {
        return ResponseEntity.ok(userMapper.map(userService.findById(UserAccessor.currentUserIdOrThrow)))
    }

    @Transactional(readOnly = true)
    override fun validation(@NotNull
                            @Pattern(regexp = "^(?=.{1,254}$)(?=.{1,64}@)[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")
                            @Size(max = 50) @RequestParam username: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(userService.isUsernameValid(username))
    }
}
