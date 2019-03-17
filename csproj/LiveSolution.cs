using System;
using System.Runtime.CompilerServices;

namespace Live {
    public class Message {
        public string Text { get; set; }
    }

    public static class MessageMixins {

        private static readonly ConditionalWeakTable<Message, object> state_ =
            new ConditionalWeakTable<Message, object>();

        public static string Escalated(this Message self) {

            object n;
            if (!state_.TryGetValue(self, out n))
                n = 0;

            string s = self.Text.ToUpper() + new String('!', (int) n);
            state_.AddOrUpdate(self, ((int) n)+1);
            return s;
        }
    }

    class Program {
        // static void Main(string[] args) {
        //     Message m = new Message();
        //     Message m2 = new Message();
            
        //     m.Text = "Hallo, Welt";
        //     m2.Text = "Meh!";

        //     Console.WriteLine(m.Text);
        //     Console.WriteLine(m.Escalated());
        //     Console.WriteLine(m.Escalated());
            
        //     Console.WriteLine(m2.Escalated());
        //     Console.WriteLine(m2.Escalated());
        // }
    }
}