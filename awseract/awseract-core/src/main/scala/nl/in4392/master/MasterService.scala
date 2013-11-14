package main.scala.nl.in4392.master

import nl.in4392.master.MasterActor
import com.typesafe.config.ConfigFactory
import akka.kernel.Bootable
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import main.scala.nl.in4392.models.Task.Task
import java.io._
import java.util.UUID
import nl.tudelft.ec2interface.taskmonitor.TaskInfo

class MasterService extends Bootable {

  val system = ActorSystem("MasterNode", ConfigFactory.load().getConfig("masterSys"))
  val masterActor = system.actorOf(Props[MasterActor], name = "masterActor")


  def startup() {
  }

  def testTasks(): Unit = {

    //masterActor ! new Task("aws-task-1","Hello Daniel! I am the first task")

    val bis = new BufferedInputStream(new FileInputStream("./src/main/resources/TEST_2.JPG"))
    val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray

    //val inputStream = new FileInputStream(imageFile)
    var tInfo = new TaskInfo()
    val taskId = UUID.randomUUID().toString
    tInfo.setUuid(taskId)
    tInfo.setMasterId("DefaultMasterID")
    tInfo.setWorkerId("DefaultWorkerID")
    tInfo.setTaskSize(12)
    //change it to correct values;

    masterActor ! new Task(taskId, byteArray, tInfo.ToJson(tInfo))
  }


  def shutdown() {
    system.shutdown()
  }
}

object MasterApp {
  def main(args: Array[String]) {
    val app = new MasterService
    println("Started Master Node")

    app.testTasks()
  }
}
