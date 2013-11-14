package main.scala.nl.in4392.models

import nl.tudelft.ec2interface.taskmonitor._
object Task {

  case class Task(taskId: String, job: Any, taskInfo: TaskInfo)
  case class TaskResult(taskId: String, result: Any, taskInfo: TaskInfo)

}
