package com.github.autoftp.client;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.SftpConnection;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Ignore
public class SftpClientTest {

	private static final String SFTP = "sftp";

	@InjectMocks
	private SftpClient sftpClient = new SftpClient();

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private JSch mockJsch;
	
	@Mock
	private Session mockSession;

	@Before
	public void setUp() throws JSchException {
		
		initMocks(this);
		
		sftpClient.setHost("host");
		sftpClient.setPort(999);
		sftpClient.setCredentials("user", "password");
	}

	@Test
	public void connectMethodShouldCreateSessionUsingHostPortAndUsername() throws JSchException {
		
		sftpClient.connect();
		
		verify(mockJsch).getSession("user", "host", 999);
	}
	
	@Test
	public void sessionFromInitialConnectionNeedsConfigAndPasswordSettingBeforeConnecting() throws JSchException {
		
		Session mockSession = mockJsch.getSession("user", "host", 999);
		
		InOrder inOrder = Mockito.inOrder(mockSession);
		
		sftpClient.connect();
		
		inOrder.verify(mockSession).setConfig("StrictHostKeyChecking", "no");
		inOrder.verify(mockSession).setPassword("password");
		inOrder.verify(mockSession).connect();
	}
	
	@Test
	public void returnedSessionObjectShouldSetChannelToSftpAndOpen() throws JSchException {
		
		Session mockSession = mockJsch.getSession("user", "host", 999);
		
		sftpClient.connect();
		
		verify(mockSession).openChannel(SFTP);
	}
	
	@Test
	public void sessionChannelShouldBeConnectedTo() throws JSchException {
		
		Session mockSession = mockJsch.getSession("user", "host", 999);
		Channel mockChannel = mockSession.openChannel(SFTP);
		
		sftpClient.connect();
		
		verify(mockChannel).connect();
	}

	@Test
	public void connectMethodShouldReturnLiveInstanceOfSftpChannelWrappedInStfpConnection() {
		
		Connection connection = sftpClient.connect();
		
		assertThat(connection, is(instanceOf(SftpConnection.class)));
	}
}
