using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;

namespace Mixins {
    class Message
    {
        public string Text { set; get; }

        public void Sophisticated(int o) {
            Console.WriteLine("Message.Sophisticated: {0}", o.GetType());
        }
    }

    static class EscalateMixin {
        // C# weak references ftw!
        private static readonly ConditionalWeakTable<Message, object> state_ =
            new ConditionalWeakTable<Message, object>();

        // bad idea: dangling references
        private static readonly Dictionary<Message, int> bad_state_ =
            new Dictionary<Message, int>();

        public static void Append(this Message self, Message other)
            => self.Text = self.Text + other.Text;

        // on the internet, caps escalate!
        public static string Escalated1(this Message self) 
            => self.Text.ToUpper();

        // maintain state: the more often we call, the more !!! we append!
        public static string Escalated2(this Message self) {
            object n;
            if (!state_.TryGetValue(self, out n))
                n = 0;

            string message = self.Text.ToUpper() + new String('!', (int) n);
            state_.AddOrUpdate(self, 1 + (int) n);
            return message;
        }
    }

    class Program {
        // static void Main(string[] args) {
        //     Message m = new Message();
        //     m.Text = "I'm a regular message";

        //     Console.WriteLine(m.Text);
        //     Console.WriteLine(m.Escalated1());
        //     Console.WriteLine(EscalateMixin.Escalated1(m));

        //     Console.WriteLine(m.Escalated2());
        //     Console.WriteLine(m.Escalated2());
        //     Console.WriteLine(m.Escalated2());
        //     Console.WriteLine(m.Escalated2());

        //     m.Append(m);
        //     Console.WriteLine(m.Text);
        // }
    }
}