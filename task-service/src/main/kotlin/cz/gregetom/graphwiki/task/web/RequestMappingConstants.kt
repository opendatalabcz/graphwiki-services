package cz.gregetom.graphwiki.task.web

object RequestMappingConstants {
    const val TC_CREATE = "/task"
    const val TC_SELF = "/task/*"
    const val TC_FINISHING = "/task/*"
    const val TC_ASSIGNMENT = "/task/*/assignment"
    const val TC_OPEN = "/tasks/open"
    const val TC_INBOX_PRIVATE = "/tasks/inbox/private"
    const val TC_INBOX_TEAM = "/tasks/inbox/team"
}
