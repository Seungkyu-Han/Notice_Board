plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "NoticeBoard"
include("security")
include("user")
include("user:presentation")
findProject(":user:presentation")?.name = "presentation"
include("user:core")
findProject(":user:core")?.name = "core"
include("user:persistence")
findProject(":user:persistence")?.name = "persistence"
include("aop")
include("post")
include("post:presentation")
findProject(":post:presentation")?.name = "presentation"
include("post:core")
findProject(":post:core")?.name = "core"
include("post:persistence")
findProject(":post:persistence")?.name = "persistence"
include("gateway")
