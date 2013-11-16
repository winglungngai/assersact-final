package main.scala.nl.in4392.worker
import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.actor.ActorLogging
import akka.actor.ActorPath
import main.scala.nl.in4392.models.DistributedProtocol.WorkerRegister
import main.scala.nl.in4392.models.DistributedProtocol.MonitorRegister
import scala.concurrent.duration._
import nl.tudelft.ec2interface.instancemanager._
import nl.tudelft.ec2interface.sysmonitor._

class MonitorActor(workerId:String,masterPath: ActorPath) extends Actor with ActorLogging {
  import main.scala.nl.in4392.models.DistributedProtocol.{ReportSystemInfo,RequestSystemInfo}
  import nl.tudelft.ec2interface.sysmonitor._
  import context._
  import nl.tudelft.ec2interface.instancemanager._

  val instanceId = new RemoteActorInfo().getInfoFromFile("conf/masterInfo").getSelfInstanceID

  val master = context.actorSelection(masterPath)
  // val workerId = UUID.randomUUID().toString

  override def preStart() = {
    println("MonitoringActor " + workerId + " starts")
    master ! MonitorRegister(workerId)

  }

  def receive = {
    case RequestSystemInfo =>
      val sInfo = new SystemUsage().getInfo

      sInfo.setInstanceId(instanceId)
      sInfo.setWorkerId(instanceId)
      master ! ReportSystemInfo(workerId, new SystemUsage().ToJson(sInfo))
      //println("MonitoringActor " + workerId + " sends SystemInfo")
      context.system.scheduler.scheduleOnce(20 seconds, self, RequestSystemInfo)
  }


}


