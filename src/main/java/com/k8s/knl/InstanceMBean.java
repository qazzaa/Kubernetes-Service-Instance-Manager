package com.k8s.knl;


import java.util.Map;

public interface InstanceMBean {
    public String hello();
    public String createNamespace(String namespace, String label, String selector);
    public String deleteNamespace(String namespace);
    public int namespaces(String label);
    public String readNamespace(String namespace);
    public String createNamespacedRoleBinding(String role, String namespace, String serviceAccount);
    public String createNamespacedServiceAccount(String namespace);
    public String createClusterRole(String name);
    public String createNamespacedSecret(String namespace, String serviceAccount);
    public String createNamespacedLimitRange(String namespace);
    public String readNamespacedSecret(String secret, String namespace);
    public String generateConfigFile(String secret, String namespace, String serviceAccount);
    public String createNamespacedResourceQuota(String namespace, String cpuLimit, String memoryLimit, String storage);
    public String deleteClusterRoleBinding(String name);
    public Map<String,String> resourceQuotas();
    public Map<String,String> userResourceQuotas(String label);
    public Map<String,String> resourceInfo(String selector);
}

