using System.IO;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

namespace MSBuildPlugin
{
    public class SimpleReadWrite : Task
    {
        public string ProjectPath { get; set; }
        private const string _id = "SimpleReadWrite";

        public override bool Execute()
        {
            string[] configFiles = Directory.GetFiles(ProjectPath, "*.csproj");
            BuildEngine.LogMessageEvent(new BuildMessageEventArgs("Plugin is running... ", "", _id, MessageImportance.High));
            BuildEngine.LogMessageEvent(new BuildMessageEventArgs("Project Path: " + ProjectPath, "", _id, MessageImportance.High));
            foreach (string filePath in configFiles)
            {
                string fileName = new FileInfo(filePath).Name;
                File.WriteAllText(ProjectPath + "//" + "copy-" + fileName, File.ReadAllText(filePath));
            }
            return true;
        }
    }
}
