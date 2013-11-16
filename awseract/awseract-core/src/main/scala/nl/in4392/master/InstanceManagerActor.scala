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
import nl.tudelft.ec2interface.instancemanager._
import nl.tudelft.ec2interface.logging.LogManager


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
      context.system.scheduler.scheduleOnce(600 seconds, self, ManageInstance)

    case SystemStatus(jobSize, workers) =>
      //println("print system status. and joseph did some logic here", status)
      val workers_idle = workers.filter { case (_, WorkerState(_,x)) => x == Idle }
      val jobs_count = jobSize
      println("job queue numbers",jobSize)
      val idle_size = workers_idle.size
      println("Joseph is genius ", workers_idle.size)

      val ec2 = new EC2Interface("conf/AwsCredentials.properties")

      if(workers.size < 5 )  {
        println(" > 0 ={} jobs pending or less than 1 workers {}", jobs_count, workers.size)
        val instanceId = ec2.runNewInstance("ami-028eb847");
        val masterPublicIP = new RemoteActorInfo().getInfoFromFile("conf/masterInfo").getPublicIP()
        ec2.configureInstance(masterPublicIP, instanceId, "conf/remoteConfigureWorker.sh", "conf/joseph_wing.pem");
        println("starting new worker instance ", instanceId)
        new LogManager().logInstance("start", workers.size)
      }
      else if ( idle_size > 0 )
      {
//        if ( workers.size > 1 && workers.size/idle_size < 3)
//        {
//          println("> more than 1 workers and worker/idle < 3", workers.size, idle_size)
//          val toDeleted = workers_idle.keys.last
//          ec2.terminateInstance(toDeleted)
//          masterActor ! WorkerDeregister(toDeleted)
//          println("workersSize / idleSize 3 ", workers.size , idle_size)
//          new LogManager().logInstance("terminate", workers.size)
//        }

      }

   }

}