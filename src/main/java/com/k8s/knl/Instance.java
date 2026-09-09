package com.k8s.knl;


import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Yaml;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Singleton
public class Instance implements InstanceMBean {

    public ApiClient client;
    public CoreV1Api api;

    //  c:\work\test\admin.conf
    //  /etc/kubernetes/admin.conf
    @Inject
    public Instance(@Value("${k8s.config:c:\\work\\test\\admin.conf}") String config)
            throws Exception {

        try {
            FileReader kubeConfigFileReader = new FileReader(config);
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(kubeConfigFileReader)).build();;
            Configuration.setDefaultApiClient(client);
            api = new CoreV1Api();
            api.setApiClient(client);
            System.out.println("Successful connection to k8s");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    @Override
    public String hello() {
        return "ГАМБИТ с бонпаришкой за щекою";
    }

    @Override
    public String createNamespace(String namespace, String label, String selector) {
        try {
            Map<String, String> labels = new HashMap<>();
            labels.put("vrauser", label);
            V1Namespace v1Namespace = new V1Namespace() // V1Namespace |
                    .metadata(new V1ObjectMeta()
                            .name(namespace)
                            .labels(labels));
            if(!selector.isEmpty()) {
                Map<String, String> annotations = new HashMap<>();
                annotations.put("scheduler.alpha.kubernetes.io/node-selector", selector);
                v1Namespace.getMetadata().setAnnotations(annotations);
            }
            V1Namespace result = api.createNamespace(v1Namespace, null, null, null, null);
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public String deleteNamespace(String namespace) {
        try {
            V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
            V1Status result = api.deleteNamespace(namespace, null, null, 56,
                    true, null, null, v1DeleteOptions);
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public int namespaces(String label) {
        try {
            V1NamespaceList list =
                    api.listNamespace(null, null, null, null,
                            "vrauser=" + label, null, null, null,
                            null, null, null);
            return list.getItems().size();
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return 0;
    }

    @Override
    public String readNamespace(String namespace) {
        try {
            V1Namespace result = api.readNamespace(namespace, null);
            return result.getMetadata().getName();
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "";
    }

    @Override
    public String createNamespacedRoleBinding(String role, String namespace, String serviceAccount) {
        try {
            RbacAuthorizationV1Api rbacAuthorizationV1Api = new RbacAuthorizationV1Api(client);
            V1RoleBinding v1RoleBinding = new V1RoleBinding()
                    .metadata(new V1ObjectMeta()
                            .name(role + "-" + namespace + "-" + serviceAccount)
                            .namespace(namespace))
                    .roleRef(new V1RoleRef()
                            .name(role)
                            .kind("ClusterRole")
                            .apiGroup("rbac.authorization.k8s.io"));
            RbacV1Subject rbacV1Subject = new RbacV1Subject()
                    .kind("ServiceAccount")
                    .name(serviceAccount)
                    .namespace(namespace);
            List<RbacV1Subject> subjects = new ArrayList<>();
            subjects.add(rbacV1Subject);
            v1RoleBinding.setSubjects(subjects);
            String pretty = null; // String | If 'true', then the output is pretty printed.
            String dryRun = null; // String | When present, indicates that modifications should not be persisted. An invalid or unrecognized dryRun directive will result in an error response and no further processing of the request. Valid values are: - All: all dry run stages will be processed
            String fieldManager = null; // String | fieldManager is a name associated with the actor or entity that is making these changes. The value must be less than or 128 characters long, and only contain printable characters, as defined by https://golang.org/pkg/unicode/#IsPrint.
            String fieldValidation = null; // String | fieldValidation determines how the server should respond to unknown/duplicate fields in the object in the request. Introduced as alpha in 1.23, older servers or servers with the `ServerSideFieldValidation` feature disabled will discard valid values specified in  this param and not perform any server side field validation. Valid values are: - Ignore: ignores unknown/duplicate fields. - Warn: responds with a warning for each unknown/duplicate field, but successfully serves the request. - Strict: fails the request on unknown/duplicate fields.
            V1RoleBinding result = rbacAuthorizationV1Api.createNamespacedRoleBinding(namespace, v1RoleBinding, pretty, dryRun, fieldManager, fieldValidation);
            System.out.println(String.valueOf(result));
            createClusterRoleBinding(role, namespace, serviceAccount);
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling RbacAuthorizationV1Api#createClusterRoleBinding");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    public String createClusterRoleBinding(String role, String namespace, String serviceAccount) {
        try {
            RbacAuthorizationV1Api rbacAuthorizationV1Api = new RbacAuthorizationV1Api(client);
            V1ClusterRoleBinding v1ClusterRoleBinding = new V1ClusterRoleBinding()
                    .metadata(new V1ObjectMeta()
                            .name(role + "-" + serviceAccount))
                    .roleRef(new V1RoleRef()
                            .name(role)
                            .kind("ClusterRole")
                            .apiGroup("rbac.authorization.k8s.io"));
            List<RbacV1Subject> subjects = new ArrayList<>();
            subjects.add(new RbacV1Subject()
                    .kind("ServiceAccount")
                    .name(serviceAccount)
                    .namespace(namespace));
            v1ClusterRoleBinding.setSubjects(subjects);
            String pretty = null; // String | If 'true', then the output is pretty printed.
            String dryRun = null; // String | When present, indicates that modifications should not be persisted. An invalid or unrecognized dryRun directive will result in an error response and no further processing of the request. Valid values are: - All: all dry run stages will be processed
            String fieldManager = null; // String | fieldManager is a name associated with the actor or entity that is making these changes. The value must be less than or 128 characters long, and only contain printable characters, as defined by https://golang.org/pkg/unicode/#IsPrint.
            String fieldValidation = null; // String | fieldValidation determines how the server should respond to unknown/duplicate fields in the object in the request. Introduced as alpha in 1.23, older servers or servers with the `ServerSideFieldValidation` feature disabled will discard valid values specified in  this param and not perform any server side field validation. Valid values are: - Ignore: ignores unknown/duplicate fields. - Warn: responds with a warning for each unknown/duplicate field, but successfully serves the request. - Strict: fails the request on unknown/duplicate fields.
            V1ClusterRoleBinding result = rbacAuthorizationV1Api.createClusterRoleBinding(v1ClusterRoleBinding, pretty, dryRun, fieldManager, fieldValidation);
            System.out.println(String.valueOf(result));
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling RbacAuthorizationV1Api#createClusterRoleBinding");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public String createNamespacedServiceAccount(String namespace) {
        try {
            V1ServiceAccount v1ServiceAccount = new V1ServiceAccount()
                    .metadata(new V1ObjectMeta()
                            .name(namespace + "-service"));
            V1ServiceAccount result = api.createNamespacedServiceAccount(namespace, v1ServiceAccount, null, null, null, null);
            System.out.println(String.valueOf(result));
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling RbacAuthorizationV1Api#createClusterRoleBinding");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public String createClusterRole(String name) {
        try {
            RbacAuthorizationV1Api rbacAuthorizationV1Api = new RbacAuthorizationV1Api(client);
            V1ClusterRole v1ClusterRole = new V1ClusterRole()
                    .metadata(new V1ObjectMeta()
                            .name(name));
            v1ClusterRole.setRules(rbacAuthorizationV1Api.readClusterRole("admin", null).getRules());
            String pretty = null; // String | If 'true', then the output is pretty printed.
            String dryRun = null; // String | When present, indicates that modifications should not be persisted. An invalid or unrecognized dryRun directive will result in an error response and no further processing of the request. Valid values are: - All: all dry run stages will be processed
            String fieldManager = null; // String | fieldManager is a name associated with the actor or entity that is making these changes. The value must be less than or 128 characters long, and only contain printable characters, as defined by https://golang.org/pkg/unicode/#IsPrint.
            String fieldValidation = null; // String | fieldValidation determines how the server should respond to unknown/duplicate fields in the object in the request. Introduced as alpha in 1.23, older servers or servers with the `ServerSideFieldValidation` feature disabled will discard valid values specified in  this param and not perform any server side field validation. Valid values are: - Ignore: ignores unknown/duplicate fields. - Warn: responds with a warning for each unknown/duplicate field, but successfully serves the request. - Strict: fails the request on unknown/duplicate fields.
            V1ClusterRole result = rbacAuthorizationV1Api.createClusterRole(v1ClusterRole, pretty, dryRun, fieldManager, fieldValidation);
            System.out.println(String.valueOf(result));
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling RbacAuthorizationV1Api#createClusterRoleBinding");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public String createNamespacedSecret(String namespace, String serviceAccount) {
        try {
            V1Secret v1Secret = new V1Secret()
                    .metadata(new V1ObjectMeta()
                            .namespace(namespace)
                            .name(serviceAccount + "-" + namespace + "-token")
                            .putAnnotationsItem("kubernetes.io/service-account.name", serviceAccount))
                    .type("kubernetes.io/service-account-token"); // V1Secret |
            String pretty = null; // String | If 'true', then the output is pretty printed.
            String dryRun = null; // String | When present, indicates that modifications should not be persisted. An invalid or unrecognized dryRun directive will result in an error response and no further processing of the request. Valid values are: - All: all dry run stages will be processed
            String fieldManager = null; // String | fieldManager is a name associated with the actor or entity that is making these changes. The value must be less than or 128 characters long, and only contain printable characters, as defined by https://golang.org/pkg/unicode/#IsPrint.
            String fieldValidation = null; // String | fieldValidation determines how the server should respond to unknown/duplicate fields in the object in the request. Introduced as alpha in 1.23, older servers or servers with the `ServerSideFieldValidation` feature disabled will discard valid values specified in  this param and not perform any server side field validation. Valid values are: - Ignore: ignores unknown/duplicate fields. - Warn: responds with a warning for each unknown/duplicate field, but successfully serves the request. - Strict: fails the request on unknown/duplicate fields.
            V1Secret result = api.createNamespacedSecret(namespace, v1Secret, pretty, dryRun, fieldManager, fieldValidation);
            System.out.println(String.valueOf(result));
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespacedSecret");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String createNamespacedLimitRange(String namespace) {
        try {
            V1LimitRangeItem v1LimitRangeItem = new V1LimitRangeItem()
                    .putDefaultItem("memory", new Quantity("512Mi"))
                    .putDefaultItem("cpu", new Quantity("500m"))
                    .putDefaultRequestItem("memory", new Quantity("256Mi"))
                    .putDefaultRequestItem("cpu", new Quantity("250m"))
                    .type("Container");
            V1LimitRange v1LimitRange = new V1LimitRange()
                    .apiVersion("v1")
                    .kind("LimitRange")
                    .metadata(new V1ObjectMeta().name(namespace + "-limit-range").namespace(namespace))
                    .spec(new V1LimitRangeSpec()
                            .addLimitsItem(v1LimitRangeItem));
            V1LimitRange result = api.createNamespacedLimitRange(namespace, v1LimitRange, null, null, null, null);
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public String readNamespacedSecret(String secret, String namespace) {
        try {
            V1Secret v1Secret = api.readNamespacedSecret(secret, namespace, null);
            v1Secret.getMetadata().setManagedFields(null);
            String yml = Yaml.dump(v1Secret);
//            System.out.println(new String(v1Secret.getData().get("namespace"), StandardCharsets.UTF_8));
//            System.out.println(new String(v1Secret.getData().get("ca.crt"), StandardCharsets.UTF_8));
//            System.out.println(new String(v1Secret.getData().get("token"), StandardCharsets.UTF_8));
            System.out.println(yml);
            return String.valueOf(yml);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespacedSecret");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public String generateConfigFile(String secret, String namespace, String serviceAccount) {
        try {
            V1Secret v1Secret = api.readNamespacedSecret(secret, namespace, null);
            Map<String, String> data = new HashMap<>();
            for(String key : v1Secret.getData().keySet())
                data.put(key, new String(v1Secret.getData().get(key), StandardCharsets.UTF_8));
            String config = String.format(
                    "apiVersion: v1\n" +
                            "kind: Config\n" +
                            "clusters:\n" +
                            "  - name: kubernetes-" + serviceAccount + "@kubernetes\n" +
                            "    cluster:\n" +
                            "      server: '" + client.getBasePath() + "'\n" +
                            "      certificate-authority-data: >-\n" +
                            "        %s\n" +
                            "users:\n" +
                            "  - name: " + serviceAccount + "\n" +
                            "    user:\n" +
                            "      token: >-\n" +
                            "        %s\n" +
                            "contexts:\n" +
                            "  - name: kubernetes-" + serviceAccount + "@kubernetes\n" +
                            "    context:\n" +
                            "      user: " + serviceAccount + "\n" +
                            "      cluster: kubernetes-" + serviceAccount + "@kubernetes\n" +
                            "      namespace: " + namespace + "\n" +
                            "current-context: kubernetes-" + serviceAccount + "@kubernetes\n",
                    new String(Base64.getEncoder().encode(data.get("ca.crt").getBytes()), StandardCharsets.UTF_8),
                    data.get("token")
            );
            System.out.println(config);
            return config;
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespacedSecret");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public String createNamespacedResourceQuota(String namespace, String cpuLimit, String memoryLimit, String storage) {
        try {
            V1ResourceQuota v1ResourceQuota = new V1ResourceQuota()
                    .apiVersion("v1")
                    .kind("ResourceQuota")
                    .metadata(new V1ObjectMeta().name(namespace + "-quota").namespace(namespace))
                    .spec(new V1ResourceQuotaSpec()
                            .putHardItem("limits.cpu", new Quantity(cpuLimit))
                            .putHardItem("limits.memory", new Quantity(memoryLimit + "Gi")));
//                            .putHardItem("pods", new Quantity(pods))
//                            .putHardItem("requests.storage", new Quantity(storage + "Gi")));
//                            .putHardItem("limits.ephemeral-storage", new Quantity(storage + "Gi")));
            String pretty = null; // String | If 'true', then the output is pretty printed.
            String dryRun = null; // String | When present, indicates that modifications should not be persisted. An invalid or unrecognized dryRun directive will result in an error response and no further processing of the request. Valid values are: - All: all dry run stages will be processed
            String fieldManager = null; // String | fieldManager is a name associated with the actor or entity that is making these changes. The value must be less than or 128 characters long, and only contain printable characters, as defined by https://golang.org/pkg/unicode/#IsPrint.
            String fieldValidation = null; // String | fieldValidation determines how the server should respond to unknown/duplicate fields in the object in the request. Introduced as alpha in 1.23, older servers or servers with the `ServerSideFieldValidation` feature disabled will discard valid values specified in  this param and not perform any server side field validation. Valid values are: - Ignore: ignores unknown/duplicate fields. - Warn: responds with a warning for each unknown/duplicate field, but successfully serves the request. - Strict: fails the request on unknown/duplicate fields.
            V1ResourceQuota result = api.createNamespacedResourceQuota(namespace, v1ResourceQuota, pretty, dryRun, fieldManager, fieldValidation);
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespacedSecret");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public String deleteClusterRoleBinding(String name) {
        try {
            RbacAuthorizationV1Api rbacAuthorizationV1Api = new RbacAuthorizationV1Api(client);
            V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
            V1Status result = rbacAuthorizationV1Api.deleteClusterRoleBinding(name, null, null, 56,
                    true, null, null, v1DeleteOptions);
            return String.valueOf(result);
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "Error";
    }

    @Override
    public Map<String,String> userResourceQuotas(String label) {
        try {
            Map<String,String> result = new HashMap<>();
            V1NamespaceList list =
                    api.listNamespace(null, null, null, null,
                            "vrauser=" + label, null, null, null,
                            null, null, null);
            for(V1Namespace namespace : list.getItems()) {
                V1ResourceQuotaList resourceQuotas = api.listNamespacedResourceQuota(namespace.getMetadata().getName(), null, null,
                        null, null, null, null, null, null, null,
                        null, null);
                if(resourceQuotas != null && !resourceQuotas.getItems().isEmpty())  {
                    for(V1ResourceQuota resourceQuota : resourceQuotas.getItems()) {
                        if(resourceQuota.getStatus() != null && resourceQuota.getStatus().getHard() != null) {
                            Map<String, Quantity> hardLimits = resourceQuota.getStatus().getHard();
                            for(Map.Entry<String, Quantity> entry : hardLimits.entrySet()) {
                                System.out.println(entry.getKey().substring(entry.getKey().indexOf(".")+1) + ": " + entry.getValue().getNumber());
                                String key = entry.getKey().substring(entry.getKey().indexOf(".")+1);
                                Quantity value = entry.getValue();
                                if(result.get(key)!=null) {
                                    result.put(key, String.valueOf(value.getNumber().toBigInteger().longValue() +
                                            Long.parseLong(result.get(key))));
                                } else {
                                    result.put(key, entry.getValue().getNumber().toString());
                                }
                            }
                        }
                    }
                }
            }
            return result;
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return null;
    }

    @Override
    public Map<String,String> resourceQuotas() {
        try {
            Map<String,String> result = new HashMap<>();
            V1NamespaceList list =
                    api.listNamespace(null, null, null, null,
                            null, null, null, null,
                            null, null, null);
            for(V1Namespace namespace : list.getItems()) {
                V1ResourceQuotaList resourceQuotas = api.listNamespacedResourceQuota(namespace.getMetadata().getName(), null, null,
                        null, null, null, null, null, null, null,
                        null, null);
                if(resourceQuotas != null && !resourceQuotas.getItems().isEmpty())  {
                    System.out.println("----------"+namespace.getMetadata().getName()+"----------");
                    for(V1ResourceQuota resourceQuota : resourceQuotas.getItems()) {
                        if(resourceQuota.getStatus() != null && resourceQuota.getStatus().getUsed() != null) {
                            Map<String, Quantity> usedLimits = resourceQuota.getStatus().getUsed();
                            for(Map.Entry<String, Quantity> entry : usedLimits.entrySet()) {
                                System.out.println(entry.getKey().substring(entry.getKey().indexOf(".")+1) + ": " + entry.getValue().getNumber());
                                String key = entry.getKey().substring(entry.getKey().indexOf(".")+1);
                                Quantity value = entry.getValue();
                                if(result.get(key)!=null) {
                                    result.put(key, String.valueOf(value.getNumber().toBigInteger().longValue() +
                                            Long.parseLong(result.get(key))));
                                } else {
                                    result.put(key, String.valueOf(value.getNumber().toBigInteger().longValue()));
                                }
                            }
                        }
                    }
                }
            }
            return result;
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return null;
    }


    @Override
    public Map<String, String> resourceInfo(String selector) {
        try {
            Map<String,String> result = new HashMap<>();
            V1NodeList v1NodeList =
                    api.listNode(null, null, null, null,
                            selector, null, null, null,
                            null, null, null);
            for(V1Node node : v1NodeList.getItems()) {
                System.out.println("----------"+node.getMetadata().getName()+"----------");
                for(String resource : node.getStatus().getCapacity().keySet()) {
                    System.out.println(resource.substring(resource.indexOf(".") + 1) + ": "
                            + node.getStatus().getCapacity().get(resource).getNumber());
                    String key = resource.substring(resource.indexOf(".") + 1);
                    Quantity value = node.getStatus().getCapacity().get(resource);
                    if (result.get(key) != null) {
                        result.put(key, String.valueOf(value.getNumber().toBigInteger().longValue() +
                                Long.parseLong(result.get(key))));
                    } else {
                        result.put(key, value.getNumber().toString());
                    }
                }
            }
            System.out.println("[---Total---]");
            for(String key : result.keySet())
                System.out.println(key+": "+result.get(key));
            Map<String,String> used = resourceQuotas();
            for(String key : used.keySet())
                try {
                    result.put(key, String.valueOf(Long.parseLong(result.get(key)) - Long.parseLong(used.get(key))));
                } catch (NumberFormatException e) {
                    System.out.println("NumberFormatException:" + key);
                }
            System.out.println("[---Free---]");
            for(String key : result.keySet())
                System.out.println(key+": "+result.get(key));

//            V1PersistentVolumeList v1PersistentVolumeList =
//                    api.listPersistentVolume(null, null, null, null,
//                            null, null, null, null,
//                            null, null, null);
//            for(V1PersistentVolume volume : v1PersistentVolumeList.getItems()) {
//                Map<String,Quantity>  spec = volume.getSpec().getCapacity();
//                for(Map.Entry<String, Quantity> entry : spec.entrySet()) {
//                    System.out.println(entry);
//                }
//                V1PersistentVolumeClaimList v1PersistentVolumeClaimList =
//                        api.listPersistentVolumeClaimForAllNamespaces(null, null,
//                                null, null,
//                                null, null, null, null,
//                                null, null, null);
//                System.out.println(v1PersistentVolumeClaimList);
//            }
            return result;
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: " + e.getCode());
            System.out.println("Reason: " + e.getResponseBody());
            System.out.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return Map.of();
    }
}
