package pl.fhframework.docs.application.cache;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;

/**
 * Created by Adam Zareba on 02.02.2017.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-app-cache")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class ApplicationCacheUC implements IInitialUseCase {

    private ApplicationCacheModel model = new ApplicationCacheModel();

    @Override
    public void start() {
        model.getAppProps().add(new StaticTableData("cache.cluster.name", "name of cluster of nodes with replication (empty value is equivalent of this machine name)", "empty value or machine name with 'dev' profile"));
        model.getAppProps().add(new StaticTableData("cache.cluster.force", "if 'true' use 'cache.cluster.name' even with 'dev' profile", "false"));

        model.getCacheProps().add(new StaticTableData("loginsWithWebSocket", "cache for tracing logged in users", "Asynchronous repliaction, 10 seconds eviction"));

        model.getJgroupsProps().add(new StaticTableData("jgroups.udp.mcast_addr", "IP address to use for multicast (both for communications and discovery). Must be a valid Class D IP address, suitable for IP multicast.", "228.6.7.8"));
        model.getJgroupsProps().add(new StaticTableData("jgroups.udp.mcast_port", "Port to use for multicast socket", "46655"));
        model.getJgroupsProps().add(new StaticTableData("jgroups.udp.ip_ttl", "Specifies the time-to-live (TTL) for IP multicast packets. The value here refers to the number of network hops a packet is allowed to make before it is dropped", "2"));

        showForm(ApplicationCacheForm.class, model);
    }
}
