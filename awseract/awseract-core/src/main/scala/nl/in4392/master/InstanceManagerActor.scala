package main.scala.nl.in4392.master
import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.actor.ActorLogging
import akka.actor.ActorPath
import main.scala.nl.in4392.models.DistributedProtocol.WorkerRegister
import main.scala.nl.in4392.models.DistributedProtocol.MonitorRegister
import main.scala.nl.in4392.models.DistributedProtocol._
import scala.concurrent.duration._
import nl.tudelft.ec2interface.instancemanager.EC2Interface;
import main.scala.nl.in4392.models.WorkerStatusProtocol.WorkerState
import main.scala.nl.in4392.models.WorkerStatusProtocol._


class InstanceManagerActor extends Actor with ActorLogging {
  import main.scala.nl.in4392.models.DistributedProtocol.{ReportSystemInfo,RequestSystemInfo}
  import nl.tudelft.ec2interface.sysmonitor._
  import context._

  var masterActor: ActorRef = null

  // shutdown machines, but which ones? we dont' know. because the workers dont know the instanceid?

  def receive = {

    case StartInstanceManager =>
      println(sender.toString())
      masterActor = sender
      self ! ManageInstance

    case ManageInstance =>
      println("Got a message abou the current system stat")
      masterActor ! RequestSystemStatus
      context.system.scheduler.scheduleOnce(20 seconds, self, ManageInstance)

    case SystemStatus(jobSize, workers) =>
      //println("print system status. and joseph did some logic here", status)
      val workers_idle = workers.filter { case (_, WorkerState(_,x)) => x == Idle }
      val jobs_count = jobSize
    println("job queue numbers",jobSize)
      val idle_size = workers_idle.size
      println("Joseph is genius ", workers_idle.size)

      if(idle_size > 0)
      {
        val  ec2 = new EC2Interface("conf/AwsCredentials.properties")

        if(jobs_count/workers.size > 10 )  {
          println("> 10 jobscount {} idleSize {}", jobs_count, idle_size)

          val instanceId = ec2.runNewInstance("ami-2890a66d")
          //println("starting new instance ", instanceId)
          println("starting new instance ")
        }
        else if ( (workers.size - idle_size) > 1 && jobs_count/workers.size < 1 )
        {
          //ec2.terminateInstance(workers_idle{case (x, WorkerState(any,any) => x)})
          println("< 10 jobscount {} idleSize {}", jobs_count, idle_size)
        }
      }


  }


}