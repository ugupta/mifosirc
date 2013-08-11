package org.mifos.irc;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MifosircServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException
  {
    String result = null;
    while (!("NONE".equals(result))) {
      try {
        result = LogsCollector.nextLog();
      } catch (Exception e) {
        e.printStackTrace(System.err);
      }
    }

    Queue queue = QueueFactory.getDefaultQueue();
    queue.add(
      TaskOptions.Builder.withUrl("/mifosirc")
      .countdownMillis(86400000L)
      .method(TaskOptions.Method.GET));
  }
}