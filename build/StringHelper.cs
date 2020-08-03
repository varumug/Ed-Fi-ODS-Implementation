// SPDX-License-Identifier: Apache-2.0
// Licensed to the Ed-Fi Alliance under one or more agreements.
// The Ed-Fi Alliance licenses this file to you under the Apache License, Version 2.0.
// See the LICENSE and NOTICES files in the project root for more information.

using System;

namespace DefaultNamespace
{
    public static class StringHelper
    {
        public static string RandomString(int size, bool useLowercase = true, bool useUppercase = true, bool useNumbers = true,
            bool useSpecial = false)
        {
            const string LowerCase = "abcdefghijklmnopqursuvwxyz";
            const string UpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            const string Numbers = "123456789";
            const string Specials = @"!@£$%^&*()#€";

            char[] result = new char[size];
            string charSet = "";
            Random random = new Random();
            int counter;

            // Build up the character set to choose from
            if (useLowercase)
                charSet += LowerCase;

            if (useUppercase)
                charSet += UpperCase;

            if (useNumbers)
                charSet += Numbers;

            if (useSpecial)
                charSet += Specials;

            for (counter = 0; counter < size; counter++)
            {
                result[counter] = charSet[random.Next(charSet.Length - 1)];
            }

            return result.ToString();
        }
    }
}
