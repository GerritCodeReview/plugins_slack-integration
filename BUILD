load("@rules_java//java:defs.bzl", "java_library")
load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)

gerrit_plugin(
    name = "slack-integration",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-ApiType: plugin",
        "Gerrit-PluginName: slack-integration",
        "Implementation-Title: Slack Integration",
        "Implementation-URL: https://gerrit-review.googlesource.com/#/admin/projects/plugins/slack-integration",
        "Implementation-Vendor: Cisco Systems, Inc.",
    ],
    resources = glob(["src/main/resources/**/*"]),
)

junit_tests(
    name = "slack-integration_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    tags = [
        "slack-integration",
    ],
    deps = [
        ":slack-integration__plugin_test_deps",
    ],
)

java_library(
    name = "slack-integration__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":slack-integration__plugin",
        "@mockito//jar",
        "@powermock_module_junit4//jar",
        "@powermock_api_mockito//jar",
    ],
)
