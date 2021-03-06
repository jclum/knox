package org.apache.hadoop.gateway.ssh;

import static org.junit.Assert.assertEquals;

import org.apache.hadoop.gateway.ssh.repl.KnoxTunnelShell;
import org.junit.Assert;
import org.junit.Test;

public class KnoxTunnelShellFactoryTest {

  @Test
  public void testNamePassthrough() {
    KnoxTunnelShellFactory factory = new KnoxTunnelShellFactory("foobar");
    KnoxTunnelShell command = factory.create();
    assertEquals("foobar", command.getTopologyName());
  }

}
