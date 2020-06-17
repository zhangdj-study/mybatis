/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.named_constructor_args;

import java.io.Reader;
import java.sql.Connection;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.googlecode.catchexception.apis.BDDCatchException.*;
import static org.assertj.core.api.BDDAssertions.then;

public class InvalidNamedConstructorArgsTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeClass
  public static void setUp() throws Exception {
    // create an SqlSessionFactory
    Reader reader = Resources.getResourceAsReader(
        "org/apache/ibatis/submitted/named_constructor_args/mybatis-config.xml");
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    reader.close();

    // populate in-memory database
    SqlSession session = sqlSessionFactory.openSession();
    Connection conn = session.getConnection();
    reader = Resources
        .getResourceAsReader("org/apache/ibatis/submitted/named_constructor_args/CreateDB.sql");
    ScriptRunner runner = new ScriptRunner(conn);
    runner.setLogWriter(null);
    runner.runScript(reader);
    conn.close();
    reader.close();
    session.close();
  }

  interface NoMatchingConstructorMapper {
    @ConstructorArgs({
        @Arg(column = "id", name = "noSuchConstructorArg"),
    })
    @Select("select * from users ")
    User select();
  }

  @Test
  public void noMatchingConstructorArgName() {
    Configuration configuration = sqlSessionFactory.getConfiguration();
    when(configuration).addMapper(NoMatchingConstructorMapper.class);

    then(caughtException()).isInstanceOf(BuilderException.class)
      .hasMessageContaining(
          "'org.apache.ibatis.submitted.named_constructor_args.InvalidNamedConstructorArgsTest$NoMatchingConstructorMapper.select-void'")
      .hasMessageContaining("'org.apache.ibatis.submitted.named_constructor_args.UserDO'")
      .hasMessageContaining("[noSuchConstructorArg]");
  }

  interface ConstructorWithWrongJavaType {
    // There is a constructor with arg name 'id', but
    // its type is different from the specified javaType.
    @ConstructorArgs({
        @Arg(column = "id", name = "id", javaType = Integer.class),
    })
    @Select("select * from users ")
    User select();
  }

  @Test
  public void wrongJavaType() {
    Configuration configuration = sqlSessionFactory.getConfiguration();
    when(configuration).addMapper(ConstructorWithWrongJavaType.class);
    then(caughtException()).isInstanceOf(BuilderException.class)
      .hasMessageContaining(
          "'org.apache.ibatis.submitted.named_constructor_args.InvalidNamedConstructorArgsTest$ConstructorWithWrongJavaType.select-void'")
      .hasMessageContaining("'org.apache.ibatis.submitted.named_constructor_args.UserDO'")
      .hasMessageContaining("[id]");
  }

  interface ConstructorMissingRequiresJavaType {
    // There is a constructor with arg name 'id', but its type
    // is different from the type of a property with the same name.
    // javaType is required in this case.
    // Debug log shows the detail of the matching error.
    @ConstructorArgs({
        @Arg(column = "id", name = "id"),
    })
    @Select("select * from users ")
    User select();
  }

  @Test
  public void missingRequiredJavaType() {
    Configuration configuration = sqlSessionFactory.getConfiguration();
    when(configuration).addMapper(ConstructorMissingRequiresJavaType.class);
    then(caughtException()).isInstanceOf(BuilderException.class)
      .hasMessageContaining(
            "'org.apache.ibatis.submitted.named_constructor_args.InvalidNamedConstructorArgsTest$ConstructorMissingRequiresJavaType.select-void'")
      .hasMessageContaining("'org.apache.ibatis.submitted.named_constructor_args.UserDO'")
      .hasMessageContaining("[id]");
  }
}
