package com.tgt.test

import org.apache.commons.io.IOUtils
import org.openstack4j.api.Builders
import org.openstack4j.core.transport.Config
import org.openstack4j.model.compute.Server
import org.openstack4j.openstack.OSFactory

/**
 *
 */
object Openstack4jTest {

    /*
     curl -XPOST -H "Content-Type:application/json" https://openstack.target.com:5000/v2.0/tokens -d '{"auth":{"tenantName":"PRD-spinnakeropenstack","passwordCredentials":{"username":"z001mj7","password":"********"}}}';echo
     */
    @JvmStatic fun main(args: Array<String>) {
        val endpoint = System.getenv("OS_AUTH_URL")
        val tenantName = "PRD-spinnakeropenstack"
        val username = System.getenv("OS_USERNAME")
        val password = System.getenv("OS_PASSWORD")
        OSFactory.enableHttpLoggingFilter(true)
        val client = OSFactory.builderV2().withConfig(Config.newConfig().withSSLVerificationDisabled())
                .endpoint(endpoint)
                .credentials(username, password)
                .tenantName(tenantName)
                .authenticate()

//        println(client.compute().servers().get("ae9883b7-2ee9-469b-9f0d-c5a4672cda29").addresses.getAddresses("PRD-spinnakeropenstack_network").get(0).toString())
//        println("TTE")
//        client.useRegion("TTEOSCORE1").compute().securityGroups().list().forEach { println(it.id + " " + it.name) }
//        println("TTC")
//        client.useRegion("TTCOSCORE1").compute().securityGroups().list().forEach { println(it.id + " " + it.name) }
//        client.useRegion("TTEOSCORE1")
//        println(client.compute().securityGroups().get("556f7e21-82d1-437c-9318-5ba9c73d27f9"))

        val name = "asg.yaml"
        val subname = "asg_resource.yaml"

        val template = IOUtils.toString(javaClass.getClassLoader().getResourceAsStream(name))
        println(template)
        val subtemplate = IOUtils.toString(javaClass.getClassLoader().getResourceAsStream(subname))
        println(subtemplate)

//        val yaml = Yaml()
//        val thing = yaml.load(template)
//        val json = JSONValue.toJSONString(thing)
//        client.heat().templates().validateTemplate(json)

        client.heat().templates().validateTemplate(template)

        //network is PRD-spinnakeropenstack_network
        //pool is test-pool-1465927797666
        val params = mapOf<String, String>(
                "flavor" to "m1.small",
                "image" to "ubuntu-latest",
                "internal_port" to "8000",
                "max_size" to "5",
                "min_size" to "3",
                "network" to "77bb3aeb-c1e2-4ce5-8d8f-b8e9128af651",
                "pool_id" to "87077f97-83e7-4ea1-9ca9-40dc691846db",
                "security_groups" to "sg-heat-test-1"
        )

        val create = Builders.stack()
                .name("test-asg-1")
                .template(template)
                .parameters(params)
                .files(mapOf("asg_resource.yaml" to subtemplate))
                .disableRollback(false)
                .timeoutMins(15)
                .build()
        val stack = client.useRegion("TTEOSCORE1").heat().stacks().create(create)

        println(stack.toString())

    }

}