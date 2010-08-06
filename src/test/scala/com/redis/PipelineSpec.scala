package com.redis

import org.scalatest.Spec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith


@RunWith(classOf[JUnitRunner])
class PipelineSpec extends Spec 
                   with ShouldMatchers
                   with BeforeAndAfterEach
                   with BeforeAndAfterAll {

  val r = new RedisClient("localhost", 6379)

  override def beforeEach = {
  }

  override def afterEach = {
    r.flushdb
  }

  override def afterAll = {
    r.disconnect
  }

  describe("pipeline1") {
    it("should do pipelined commands") {
      r.pipeline {
        import r._
        set("key", "debasish")
        get("key")
        get("key1")
      }.get should equal(List(Some("OK"), Some("debasish"), None))
    }
  }

  describe("pipeline2") {
    it("should do pipelined commands") {
      r.pipeline {
        import r._
        lpush("country_list", "france")
        lpush("country_list", "italy")
        lpush("country_list", "germany")
        incrby("country_count", 3)
        lrange("country_list", 0, -1)
      }.get should equal (List(Some(1), Some(2), Some(3), Some(3), Some(List(Some("germany"), Some("italy"), Some("france")))))
    }
  }

  describe("pipeline3") {
    it("should do pipelined commands") {
      val thrown = 
        evaluating {
          r.pipeline {
            import r._
            set("a", "abc")
            lpop("a")
          }
        } should produce [Exception]
      thrown.getMessage should equal ("ERR Operation against a key holding the wrong kind of value")
    }
  }
}