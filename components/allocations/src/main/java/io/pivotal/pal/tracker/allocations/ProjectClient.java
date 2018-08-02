package io.pivotal.pal.tracker.allocations;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private final Map<Long,ProjectInfo> map = new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo info =  restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        if(!map.containsKey(projectId))
            map.put(projectId, info);
        return info;
    }

    public ProjectInfo getProjectFromCache(long projectId){
        return map.get(projectId);
    }
}
