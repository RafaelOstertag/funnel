package ch.guengel.funnel.build

data class MavenInfo(
    val groupId: String,
    val artifactId: String,
    val version: String
) {
    constructor() : this("n/a", "n/a", "n/a")
}