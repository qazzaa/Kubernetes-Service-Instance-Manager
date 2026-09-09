package com.k8s.rbac;

import com.k8s.knl.Instance;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

import java.util.Map;

@Controller("/k8s/rbac")
public class RbacController {

    public final Instance instance;

    @Inject
    public RbacController(Instance instance) {
        this.instance = instance;
    }

    @Post("/namespaces")
    public int namespaces(String label) {
        return instance.namespaces(label);
    }

    @Get("/resourceQuotas")
    public Map<String,String> resourceQuotas() {
        return instance.resourceQuotas();
    }

    @Post("/userResourceQuotas")
    public Map<String,String> userResourceQuotas(String label) {
        return instance.userResourceQuotas(label);
    }

    @Post("/resourceInfo")
    public Map<String,String> resourceInfo(String selector) {
        return instance.resourceInfo(selector);
    }

    @Post("/readNamespace")
    public String readNamespace(String namespace) {
        return instance.readNamespace(namespace);
    }

    @Post("/createNameSpace")
    public String createNameSpace(String namespace, String label, String selector) {
        return instance.createNamespace(namespace, label, selector);
    }

    @Post("/createNamespacedLimitRange")
    public String createNamespacedLimitRange(String namespace) {
        return instance.createNamespacedLimitRange(namespace);
    }

    @Post("/deleteNameSpace")
    public String deleteNameSpace(String namespace) {
        return instance.deleteNamespace(namespace);
    }

    @Post("/createNamespacedRoleBinding")
    public String createNamespacedRoleBinding(String role, String namespace, String serviceAccount) {
        return instance.createNamespacedRoleBinding(role, namespace, serviceAccount);
    }

    @Post("/createClusterRoleBinding")
    public String createClusterRoleBinding(String role, String namespace, String serviceAccount) {
        return instance.createClusterRoleBinding(role, namespace, serviceAccount);
    }

    @Post("/createNamespacedServiceAccount")
    public String createNamespacedServiceAccount(String namespace) {
        return instance.createNamespacedServiceAccount(namespace);
    }

    @Post("/createClusterRole")
    public String createClusterRole(String name) {
        return instance.createClusterRole(name);
    }

    @Post("/createNamespacedSecret")
    public String createNamespacedSecret(String namespace, String serviceAccount) {
        return instance.createNamespacedSecret(namespace, serviceAccount);
    }

    @Post("/readNamespacedSecret")
    public String readNamespacedSecret(String secret, String namespace) {
        return instance.readNamespacedSecret(secret, namespace);
    }

    @Post("/generateConfigFile")
    public String generateConfigFile(String secret, String namespace, String serviceAccount) {
        return instance.generateConfigFile(secret, namespace, serviceAccount);
    }

    @Post("/createNamespacedResourceQuota")
    public String createNamespacedResourceQuota(String namespace, String cpuLimit, String memoryLimit, String storage) {
        return instance.createNamespacedResourceQuota(namespace, cpuLimit, memoryLimit, storage);
    }

    @Post("/deleteClusterRoleBinding")
    public String deleteClusterRoleBinding(String name) {
        return instance.deleteClusterRoleBinding(name);
    }

    @Get("/hello")
    public String getHello(){
        return "Перемога!!!";
    }

}
