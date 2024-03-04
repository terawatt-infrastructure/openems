package io.openems.edge.bridge.http.api;

import static io.openems.edge.bridge.http.time.DelayTimeProviderChain.fixedDelay;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.openems.common.utils.ReflectionUtils;
import io.openems.edge.bridge.http.BridgeHttpImpl;
import io.openems.edge.bridge.http.CycleSubscriber;
import io.openems.edge.bridge.http.DummyUrlFetcher;

public class BridgeHttpTimeTest {

	private DummyUrlFetcher fetcher;
	private BridgeHttp bridgeHttp;

	@Before
	public void before() throws Exception {
		final var cycleSubscriber = new CycleSubscriber();
		this.bridgeHttp = new BridgeHttpImpl();
		ReflectionUtils.setAttribute(BridgeHttpImpl.class, this.bridgeHttp, "cycleSubscriber", cycleSubscriber);

		this.fetcher = new DummyUrlFetcher();
		this.fetcher.addEndpointHandler(endpoint -> {
			return switch (endpoint.url()) {
			case "dummy" -> "success";
			case "error" -> throw new RuntimeException();
			default -> null;
			};
		});
		ReflectionUtils.setAttribute(BridgeHttpImpl.class, this.bridgeHttp, "urlFetcher", this.fetcher);

		((BridgeHttpImpl) this.bridgeHttp).activate();
	}

	@After
	public void after() throws Exception {
		((BridgeHttpImpl) this.bridgeHttp).deactivate();
	}

	@Test(timeout = 1000L)
	public void testSubscribeTime() throws Exception {
		this.fetcher.addEndpointHandler(endpoint -> {
			if (!endpoint.url().equals("dummy")) {
				return null;
			}

			return "success";
		});

		final var executedOnce = new CompletableFuture<Void>();
		this.bridgeHttp.subscribeTime(fixedDelay(Duration.ofHours(99)), "dummy", result -> {
			executedOnce.complete(null);
		});

		executedOnce.get();
	}

}
