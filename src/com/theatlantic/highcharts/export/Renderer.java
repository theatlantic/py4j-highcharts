/*
  Modified by The Atlantic Media Company from original source.
  See <https://github.com/one2team/highcharts-serverside-export> for original.
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this work except in compliance with the License. You may obtain a copy
  of the License in the LICENSE file, or at:
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*/
package com.theatlantic.highcharts.export;

import java.io.OutputStream;

public interface Renderer {

	Renderer setChartOptions(String options);

	Renderer setOutputStream(OutputStream outputStream);

	Renderer setGlobalOptions(String options);

	void render();
	
	public static abstract class ChartRenderer implements Renderer {

		@Override
		public Renderer setChartOptions(String options) {
			this.options = options;
			return this;
		}

		@Override
		public Renderer setOutputStream(OutputStream output) {
			this.output = output;
			return this;
		}

		@Override
		public Renderer setGlobalOptions(String options) {
			this.globalOptions = options;
			return this;
		}

		protected String getChartOptions() {
			return options;
		}

		protected OutputStream getOutputStream() {
			return output;
		}

		protected String getGlobalOptions() {
			return globalOptions;
		}

		private String options, globalOptions;

		private OutputStream output;

	}

}