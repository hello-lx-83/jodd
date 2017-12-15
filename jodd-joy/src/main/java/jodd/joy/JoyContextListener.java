// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.joy;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.EnumSet;

import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.INCLUDE;
import static javax.servlet.DispatcherType.REQUEST;

/**
 * Joy context listener. You can use it in several ways:
 * <ul>
 *     <li>Add it in the web.xml</li>
 *     <li>Make a subclass and annotate it with @WebListener</li>
 * </ul>
 */
public class JoyContextListener implements ServletContextListener {

	protected boolean decoraEnabled = false;
	protected String context = "/*";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();

		JoddJoy joy = createJoy();

		configureServletContext(servletContext);

		joy.start(servletContext);
	}

	/**
	 * Creates {@link JoddJoy}. This is a place where user can configure the app.
	 */
	protected JoddJoy createJoy() {
		return JoddJoy.get();
	}

	/**
	 * Enables Decora.
	 */
	protected void enableDecora() {
		decoraEnabled = true;
	}

	/**
	 * Configures servlet context.
	 */
	protected void configureServletContext(ServletContext servletContext) {
		servletContext.addListener(jodd.servlet.RequestContextListener.class);

		if (decoraEnabled) {
			FilterRegistration filter = servletContext.addFilter("decora", jodd.decora.DecoraServletFilter.class);
			filter.addMappingForUrlPatterns(null, true, context);
		}

		FilterRegistration filter = servletContext.addFilter("madvoc", jodd.madvoc.MadvocServletFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, INCLUDE), true, context);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		JoddJoy.get().stop();
	}
}
