/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.gateway.shell.hdfs;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.gateway.shell.AbstractRequest;
import org.apache.hadoop.gateway.shell.AbstractResponse;
import org.apache.hadoop.gateway.shell.Hadoop;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class Get {

  static class Request extends AbstractRequest<Response> {

    private String from;
    private String to;

    Request( Hadoop hadoop ) {
      super( hadoop );
    }

    public Request from( String file ) {
      this.from = file;
      return this;
    }

    public Request file( String file ) {
      this.to = file;
      return this;
    }


    protected Callable<Response> callable() {
      return new Callable<Response>() {
        @Override
        public Response call() throws Exception {
          URIBuilder uri = uri( Hdfs.SERVICE_PATH, from );
          addQueryParam( uri, "op", "OPEN" );
          HttpGet request = new HttpGet( uri.build() );
          return new Response( execute( request ), to );
        }
      };
    }

  }

  static class Response extends AbstractResponse {

    Response( HttpResponse response, String to ) throws IOException {
      super( response );
      if( to != null ) {
        FileUtils.copyInputStreamToFile( getStream(), new File( to ) );
      }
    }

  }

}
