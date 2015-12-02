package org.infinispan.tasks;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.infinispan.tasks.spi.TaskEngine;

/**
 * TaskManager. Allows executing tasks and retrieving the list of currently running tasks
 *
 * @author Tristan Tarrant
 * @since 8.1
 */
public interface TaskManager {

   /**
    * Executes the named task, passing an optional cache and a map of named parameters.
    *
    * @param taskName
    * @param context
    * @return
    */
   <T> CompletableFuture<T> runTask(String taskName, TaskContext context);

   /**
    * Retrieves the currently executing tasks. If running in a cluster this operation
    * will return all of the tasks running on all nodes
    *
    * @return a list of {@link TaskExecution} elements
    */
   Collection<TaskExecution> getCurrentTasks();

   /**
    * Retrieves the installed task engines
    */
   Collection<TaskEngine> getEngines();
}