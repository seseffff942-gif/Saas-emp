import { createClient } from '@supabase/supabase-js';

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL || 'https://znwhvfzsvzllkfyqrvek.supabase.co';
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inpud2h2ZnpzdnpsbGtmeXFydmVrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODI2NTQ4MDIsImV4cCI6MjA5ODIzMDgwMn0.zlO5RopowmVidiNmobyzBbzDn1duUYQNKBEskOPmm4w';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);
