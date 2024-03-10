namespace Backend;

using System.Collections;
using System.Diagnostics;
using System.Management;
using System.Runtime.Versioning;

/// <summary>
/// Class model for an application
/// </summary>
public class Application
{
    private Process? _Process;
    public string Name;
    public bool Tracked;
    public DateTime StartTime;
    public DateTime EndTime;
    public TimeSpan Elapsed;
    public Category Category;

    /// <summary>
    /// Instantiates an application
    /// </summary>
    /// <param name="name"> Name of a running executable </param>
    public Application(string name)
    {
        Name = name;
        _Process = null;
        Tracked = false;
        StartTime = DateTime.Now;
        Elapsed = TimeSpan.Zero;
        Category = Category.GetInstance(Category.DEFAULT_NAME);
    }

    /// <summary>
    /// Attempt to retrieve the parent process for a running executable with the specified name and returns true. 
    /// Returns false if no such process exists.
    /// </summary>
    /// <param name="name"></param>
    /// <returns></returns>
    [SupportedOSPlatform("windows")]
    public static bool TryGetParentProcess(string name, out Process? parentProcess)
    {
        Process[] processes = Process.GetProcessesByName(name);
        if (processes.Length == 0)
        {
            parentProcess = null;
            return false;
        }
        else if (processes.Length == 1)    // Case where only parent process exists
        {
            parentProcess = processes[0];
            return true;
        }

        else
        {
            // Query WMI to get the ParentProcessId for 2 processes
            (int processId, uint parentProcessId)[] parentProcessIds = new (int, uint)[2];
            for (int i = 0; i < 2; i++)
            {
                string query = string.Format("SELECT ParentProcessId FROM Win32_Process WHERE ProcessId = {0}", processes[i].Id);
                ManagementObjectSearcher managementObjectSearcher = new ManagementObjectSearcher(query);
                ManagementObjectCollection results = managementObjectSearcher.Get();
                ManagementObjectCollection.ManagementObjectEnumerator enumerator = results.GetEnumerator();
                enumerator.MoveNext();
                parentProcessIds[i] = (processes[i].Id, (uint)enumerator.Current["ParentProcessId"]);
            }

            // Find the true parent process id. Assumes that processes with the same name cannot have different parent processes
            if (parentProcessIds[0].parentProcessId == parentProcessIds[1].parentProcessId)    // Case 1: Both proccesses have the same parent
                parentProcess = Process.GetProcessById((int)parentProcessIds[0].parentProcessId);
            else
            {
                if (parentProcessIds[0].parentProcessId == parentProcessIds[1].processId)   // Case 2: Process 1 is process 0's parent
                    parentProcess = Process.GetProcessById((int)parentProcessIds[0].parentProcessId);
                else
                    parentProcess = Process.GetProcessById((int)parentProcessIds[1].parentProcessId);    // Case 3: Process 0 is process 1's parent
            }
            return true;
        }
    }

    /// <summary>
    /// Returns whether the application instance is associated with a running process
    /// </summary>
    /// <remarks> If the application is not running (not associated with a process) this function will return false </remarks>
    /// <returns> bool </returns>
    public bool IsApplicationRunning()
    {
        if (_Process is null)
            return false;
        _Process.Refresh();
        return _Process.HasExited;
    }

    /// <summary>
    /// Returns the process id for a running application
    /// </summary>
    /// <returns></returns>
    public int GetProcessId()
    {
        return _Process.Id;
    }

    /// <summary>
    /// Calculates the amount of time elapsed for the application so far
    /// </summary>
    /// <remarks> Requires: endTime is always chronologically later than Application.StartTime </remarks>
    /// <param name="endTime"></param>
    /// <returns> TimeSpan object containing the time elapsed </returns>
    public TimeSpan CalculateTimeElapsed(DateTime endTime)
    {
        if (Tracked)
            return endTime.Subtract(StartTime);
        else
            return EndTime.Subtract(StartTime);
    }

    /// <summary>
    /// Changes the category field for this instance to the Category instance with the specified name
    /// </summary>
    /// <remarks>
    /// Requires: name is a valid name for a Category
    /// </remarks>
    /// <param name="name"></param>
    /// <exception cref="ArgumentException"></exception>
    public void ModifyCategory(string name)
    {
        Category = Category.GetInstance(name);
    }
}