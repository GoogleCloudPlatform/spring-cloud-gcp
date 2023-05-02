# Copyright 2023 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

load("@rules_gapic//:gapic.bzl", "proto_custom_library")

def _java_gapic_spring_postprocess_srcjar_impl(ctx):
    gapic_srcjar = ctx.file.gapic_srcjar
    output_srcjar_name = ctx.label.name
    srcjar_name = output_srcjar_name + "_raw.srcjar"
    output_spring = ctx.outputs.spring
    formatter = ctx.executable.formatter

    output_dir_name = ctx.label.name
    output_dir_path = "%s/%s" % (output_spring.dirname, output_dir_name)

    script = """
    WORKING_DIR=`pwd`
    cd $WORKING_DIR
    unzip -q {gapic_srcjar}
    unzip -q temp-codegen-spring.srcjar -d {output_dir_path}
    # This may fail if there are spaces and/or too many files (exceed max length of command length).
    {formatter} --replace $(find {output_dir_path} -type f -printf "%p ")

    # Spring source files.
    cd {output_dir_path}
    zip -r $WORKING_DIR/{output_srcjar_name}.srcjar ./

    cd $WORKING_DIR

    mv $WORKING_DIR/{output_srcjar_name}.srcjar {output_spring}
    """.format(
        output_srcjar_name = output_srcjar_name,
        gapic_srcjar = gapic_srcjar.path,
        srcjar_name = srcjar_name,
        formatter = formatter,
        output_dir_name = output_dir_name,
        output_dir_path = output_dir_path,
        output_spring = output_spring.path,
    )

    ctx.actions.run_shell(
        inputs = [gapic_srcjar],
        tools = [formatter],
        command = script,
        outputs = [output_spring],
    )

_java_gapic_spring_postprocess_srcjar = rule(
    attrs = {
        "gapic_srcjar": attr.label(mandatory = True, allow_single_file = True),
        "formatter": attr.label(
            default = Label("//:google_java_format_binary"),
            executable = True,
            cfg = "host",
        ),
    },
    outputs = {
        "spring": "%{name}-spring.srcjar",
    },
    implementation = _java_gapic_spring_postprocess_srcjar_impl,
)

def java_gapic_spring_library(
        name,
        srcs,
        grpc_service_config = None,
        gapic_yaml = None,
        service_yaml = None,
        transport = None,
        **kwargs):
    library_name = name + "-spring"
    raw_srcjar_name = name + "_raw"

    _java_gapic_spring_srcjar(
        name = raw_srcjar_name,
        srcs = srcs,
        grpc_service_config = grpc_service_config,
        gapic_yaml = gapic_yaml,
        service_yaml = service_yaml,
        transport = transport,
        **kwargs
    )

    _java_gapic_spring_postprocess_srcjar(
        name = name,
        gapic_srcjar = "%s.srcjar" % raw_srcjar_name,
        **kwargs
    )

def _java_gapic_spring_srcjar(
        name,
        srcs,
        grpc_service_config,
        gapic_yaml,
        service_yaml,
        transport,
        # Can be used to provide a java_library with a customized generator,
        # like the one which dumps descriptor to a file for future debugging.
        java_generator_name = "java_gapic_spring",
        **kwargs):
    file_args_dict = {}

    if grpc_service_config:
        file_args_dict[grpc_service_config] = "grpc-service-config"
    elif not transport or transport == "grpc":
        if "library" not in name:
            fail("Missing a gRPC service config file")

    if gapic_yaml:
        file_args_dict[gapic_yaml] = "gapic-config"

    if service_yaml:
        file_args_dict[service_yaml] = "api-service-config"

    opt_args = []

    if transport:
        opt_args.append("transport=%s" % transport)

    # Produces the GAPIC metadata file if this flag is set. to any value.
    # Protoc invocation: --java_gapic_opt=metadata
    plugin_args = ["metadata"]

    proto_custom_library(
        name = name,
        deps = srcs,
        plugin = Label("@spring_cloud_generator//:protoc-gen-%s" % java_generator_name),
        plugin_args = plugin_args,
        plugin_file_args = {},
        opt_file_args = file_args_dict,
        output_type = java_generator_name,
        output_suffix = ".srcjar",
        opt_args = opt_args,
        **kwargs
    )
