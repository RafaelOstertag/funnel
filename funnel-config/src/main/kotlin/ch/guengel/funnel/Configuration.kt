package ch.guengel.funnel

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.Spec

fun readConfiguration(spec: Spec, yamlFile: String): Config =
    Config { addSpec(spec) }
        .from.yaml.file(yamlFile)
        .from.systemProperties()
        .from.env()

fun readConfiguration(spec: Spec): Config =
    Config { addSpec(spec) }
        .from.systemProperties()
        .from.env()